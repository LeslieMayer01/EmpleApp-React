package proy.EmpleApp;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import proy.EmpleApp.modelo.*;
import proy.EmpleApp.repositorio.*;
import proy.EmpleApp.servicio.IUsuarioServicio;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.junit.jupiter.api.Assertions.*;

/** Usa la cadena de seguridad completa, no controladores aislados. Los datos se revierten con H2. */
@SpringBootTest
@Transactional
class SeguridadTests {
    @Autowired WebApplicationContext contexto;
    @Autowired IUsuarioServicio servicio;
    @Autowired IRolRepositorio roles;
    @Autowired IUsuarioRepositorio usuarios;
    @Autowired ITrabajadorRepositorio trabajadores;
    @Autowired IEmpleadorRepositorio empleadores;
    MockMvc mvc;

    @BeforeEach
    void preparar() {
        SecurityContextHolder.clearContext();
        mvc = MockMvcBuilders.webAppContextSetup(contexto).apply(springSecurity()).build();
    }

    @AfterEach
    void limpiarContexto() { SecurityContextHolder.clearContext(); }

    Usuario cuenta(String nombreRol, String correo) {
        Rol rol = new Rol(); rol.setNombreRol(nombreRol); roles.saveAndFlush(rol);
        Usuario usuario = new Usuario(); usuario.setNombre("Cuenta de seguridad");
        usuario.setCorreo(correo); usuario.setContrasenaHash("ClaveSegura123"); usuario.setRol(rol);
        return servicio.registrar(usuario);
    }

    @Test
    void anonimoNoPuedeLeerNiModificarDatosNiEntrarPorThymeleaf() throws Exception {
        for (String ruta : java.util.List.of("/api/usuario", "/api/usuario/me", "/api/trabajador",
                "/api/empleador", "/api/pago", "/api/servicio", "/usuario/consultar")) {
            mvc.perform(get(ruta)).andExpect(status().isUnauthorized());
        }
        mvc.perform(post("/api/usuario").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{}")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/rol")).andExpect(status().isOk());
    }

    @Test
    void loginMantieneSesionRotaIdYLogoutLaInvalida() throws Exception {
        Usuario usuario = cuenta("Trabajador", "sesion@example.test");
        var tokenRespuesta = mvc.perform(get("/api/usuario/csrf"))
            .andExpect(status().isOk()).andReturn();
        MockHttpSession sesion = (MockHttpSession) tokenRespuesta.getRequest().getSession();
        String anterior = sesion.getId();
        String token = JsonPath.read(tokenRespuesta.getResponse().getContentAsString(), "$.token");
        mvc.perform(post("/api/usuario/login").session(sesion).header("X-CSRF-TOKEN", token)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"correo\":\"sesion@example.test\",\"contrasena\":\"ClaveSegura123\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.contrasenaHash").doesNotExist());
        assertNotEquals(anterior, sesion.getId());
        mvc.perform(get("/api/usuario/me").session(sesion)).andExpect(status().isOk())
            .andExpect(jsonPath("$.idUsuario").value(usuario.getIdUsuario()));
        // El token previo al login fue retirado.
        mvc.perform(post("/api/usuario/logout").session(sesion).header("X-CSRF-TOKEN", token))
            .andExpect(status().isForbidden());
        var nuevo = mvc.perform(get("/api/usuario/csrf").session(sesion)).andReturn();
        String tokenNuevo = JsonPath.read(nuevo.getResponse().getContentAsString(), "$.token");
        mvc.perform(post("/api/usuario/logout").session(sesion).header("X-CSRF-TOKEN", tokenNuevo)
            .header("Origin", "http://localhost:5173"))
            .andExpect(status().isNoContent())
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
            .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
        assertTrue(sesion.isInvalid());
        mvc.perform(get("/api/usuario/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void csrfProtegeLoginRegistroYModificaciones() throws Exception {
        for (String ruta : java.util.List.of("/api/usuario/login", "/api/usuario/registro")) {
            mvc.perform(post(ruta).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        }
        mvc.perform(options("/api/usuario/login").header("Origin", "http://localhost:5173")
            .header("Access-Control-Request-Method", "POST"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
        mvc.perform(options("/api/usuario/logout").header("Origin", "http://localhost:5173")
            .header("Access-Control-Request-Method", "POST")
            .header("Access-Control-Request-Headers", "X-CSRF-TOKEN"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
        mvc.perform(options("/api/usuario/logout").header("Origin", "https://otro-sitio.example")
            .header("Access-Control-Request-Method", "POST")).andExpect(status().isForbidden());
        mvc.perform(options("/api/usuario/login").header("Origin", "http://sitio-ajeno.test")
            .header("Access-Control-Request-Method", "POST"))
            .andExpect(status().isForbidden());
    }

    @Test
    void trabajadorSoloEditaSuPerfilSinCambiarPropietarioNiRol() throws Exception {
        Usuario propio = cuenta("Trabajador", "propio@example.test");
        Usuario ajeno = cuenta("Trabajador", "ajeno@example.test");
        var identidad = user(propio.getIdUsuario().toString());
        mvc.perform(get("/api/trabajador/me").with(identidad))
            .andExpect(status().isOk()).andExpect(jsonPath("$.usuario.idUsuario").value(propio.getIdUsuario()));
        mvc.perform(put("/api/trabajador/me").with(identidad).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"experiencia\":\"Experiencia propia\",\"disponibilidad\":\"Mañanas\","
                + "\"usuario\":{\"idUsuario\":" + ajeno.getIdUsuario() + "},\"idTrabajador\":999}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.usuario.idUsuario").value(propio.getIdUsuario()));
        assertNull(trabajadores.findFirstByUsuario_IdUsuarioOrderByIdTrabajadorAsc(ajeno.getIdUsuario())
            .orElseThrow().getExperiencia());
        mvc.perform(get("/api/usuario/" + ajeno.getIdUsuario()).with(identidad))
            .andExpect(status().isForbidden());
        mvc.perform(put("/api/usuario/" + propio.getIdUsuario()).with(identidad).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isForbidden());
        mvc.perform(get("/api/empleador/me").with(identidad)).andExpect(status().isForbidden());
        mvc.perform(get("/api/servicio").with(identidad)).andExpect(status().isOk());
        mvc.perform(get("/api/trabajador").with(identidad)).andExpect(status().isForbidden());
    }

    @Test
    void empleadorActualizaUnicamenteSusDatos() throws Exception {
        Usuario propio = cuenta("Empleador", "empleador@example.test");
        mvc.perform(put("/api/empleador/me").with(user(propio.getIdUsuario().toString())).with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"direccion\":\"Calle de prueba\",\"telefono\":\"3001234567\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.telefono").value("3001234567"));
        assertEquals("Calle de prueba", empleadores.findFirstByUsuario_IdUsuarioOrderByIdEmpleadorAsc(
            propio.getIdUsuario()).orElseThrow().getDireccion());
    }

    @Test
    void permisosAdministrativosSeRevisanConElRolActualDeLaBase() throws Exception {
        Usuario admin = cuenta("Administrador Principal", "admin@example.test");
        var identidad = user(admin.getIdUsuario().toString());
        mvc.perform(get("/api/usuario").with(identidad)).andExpect(status().isOk());
        mvc.perform(post("/api/rol").with(identidad).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"nombreRol\":\"Rol de prueba\"}")).andExpect(status().isCreated());
        Rol trabajador = new Rol(); trabajador.setNombreRol("Trabajador"); roles.save(trabajador);
        admin.setRol(trabajador); usuarios.saveAndFlush(admin);
        mvc.perform(get("/api/usuario").with(identidad)).andExpect(status().isForbidden());
        usuarios.delete(admin); usuarios.flush();
        mvc.perform(get("/api/usuario/me").with(identidad)).andExpect(status().isUnauthorized());
    }
}
