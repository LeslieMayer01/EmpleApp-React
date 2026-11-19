package proy.EmpleApp;

import com.jayway.jsonpath.JsonPath;
import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import proy.EmpleApp.dto.*;
import proy.EmpleApp.modelo.*;
import proy.EmpleApp.repositorio.*;
import proy.EmpleApp.servicio.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.junit.jupiter.api.Assertions.*;

/** Propiedad, filtros y estados se comprueban con seguridad real y H2; cada prueba se revierte. */
@SpringBootTest
@Transactional
class PublicacionesTests {
    @Autowired WebApplicationContext contexto;
    @Autowired IRolRepositorio roles;
    @Autowired IUsuarioServicio usuarios;
    @Autowired IEmpleadorRepositorio empleadores;
    @Autowired ITrabajadorRepositorio trabajadores;
    @Autowired ITipoServicioRepositorio tipos;
    @Autowired IServicioRepositorio ofertas;
    @Autowired IPostulacionRepositorio postulaciones;
    @Autowired IContratacionRepositorio contrataciones;
    @Autowired IServicioServicio servicios;
    Usuario empleador, otro, trabajador;
    TipoServicio tipo;
    MockMvc mvc;

    @BeforeEach
    void preparar() {
        SecurityContextHolder.clearContext();
        mvc = MockMvcBuilders.webAppContextSetup(contexto).apply(springSecurity()).build();
        empleador = cuenta("Empleador", "uno@example.test");
        otro = cuenta("Empleador", "dos@example.test");
        trabajador = cuenta("Trabajador", "trabajador@example.test");
        tipo = new TipoServicio(); tipo.setNombreTipo("Aseo"); tipos.saveAndFlush(tipo);
    }

    Usuario cuenta(String nombreRol, String correo) {
        Rol rol = new Rol(); rol.setNombreRol(nombreRol); roles.saveAndFlush(rol);
        Usuario u = new Usuario(); u.setNombre(nombreRol + " de prueba"); u.setCorreo(correo);
        u.setContrasenaHash("ClaveDePrueba123"); u.setRol(rol);
        return usuarios.registrar(u);
    }

    Authentication identidad(Usuario usuario) {
        return new UsernamePasswordAuthenticationToken(usuario.getIdUsuario().toString(), null, java.util.List.of());
    }

    DatosPublicacion datos(String ubicacion) {
        return new DatosPublicacion(tipo.getIdTipoServicio(), LocalDate.now().plusDays(10),
            LocalTime.of(8, 0), LocalTime.of(12, 0), ubicacion, new BigDecimal("100000.00"));
    }

    String cuerpo() {
        return "{\"idTipoServicio\":" + tipo.getIdTipoServicio() + ",\"fecha\":\""
            + LocalDate.now().plusDays(10) + "\",\"horaInicio\":\"08:00\",\"horaFin\":\"12:00\","
            + "\"ubicacion\":\"Medellín\",\"valorOfertado\":100000}";
    }

    @Test
    void publicarTomaPropietarioYEstadoDelServidorYOcultaDatosPersonales() throws Exception {
        Integer ajeno = empleadores.findFirstByUsuario_IdUsuarioOrderByIdEmpleadorAsc(otro.getIdUsuario())
            .orElseThrow().getIdEmpleador();
        String intento = cuerpo().replace("}", ",\"empleador\":{\"idEmpleador\":" + ajeno
            + "},\"idServicio\":999,\"estado\":{\"idEstado\":999}}");
        var respuesta = mvc.perform(post("/api/servicio/mis-publicaciones")
            .with(user(empleador.getIdUsuario().toString())).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content(intento))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.estado.nombreEstado").value("Publicado"))
            .andExpect(jsonPath("$.empleador.usuario.correo").doesNotExist())
            .andExpect(jsonPath("$.empleador.telefono").doesNotExist())
            .andExpect(jsonPath("$.empleador.direccion").doesNotExist()).andReturn();
        Integer id = JsonPath.read(respuesta.getResponse().getContentAsString(), "$.idServicio");
        assertNotEquals(999, id);
        assertEquals(empleador.getIdUsuario(), ofertas.findById(id).orElseThrow().getEmpleador().getUsuario().getIdUsuario());
        // Las rutas antiguas de lectura tampoco deben devolver el grafo completo de entidades.
        mvc.perform(get("/api/servicio/" + id).with(user(trabajador.getIdUsuario().toString())))
            .andExpect(status().isOk()).andExpect(jsonPath("$.empleador.usuario.correo").doesNotExist());
        mvc.perform(get("/api/servicio").with(user(trabajador.getIdUsuario().toString())))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].empleador.usuario.rol").doesNotExist());
    }

    @Test
    void trabajadorYAnonimoNoPuedenPublicar() throws Exception {
        mvc.perform(post("/api/servicio/mis-publicaciones").with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content(cuerpo())).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/servicio/mis-publicaciones").with(csrf())
            .with(user(trabajador.getIdUsuario().toString())).contentType(MediaType.APPLICATION_JSON)
            .content(cuerpo())).andExpect(status().isForbidden());
        assertEquals(0, ofertas.count());
    }

    @Test
    void cadaEmpleadorSoloListaYModificaSusOfertas() throws Exception {
        var propia = servicios.publicar(datos("Medellín"), identidad(empleador));
        servicios.publicar(datos("Bogotá"), identidad(otro));
        mvc.perform(get("/api/servicio/mis-publicaciones").with(user(empleador.getIdUsuario().toString())))
            .andExpect(status().isOk()).andExpect(jsonPath("$.totalElementos").value(1))
            .andExpect(jsonPath("$.contenido[0].idServicio").value(propia.idServicio()));
        mvc.perform(put("/api/servicio/mis-publicaciones/" + propia.idServicio()).with(csrf())
            .with(user(otro.getIdUsuario().toString())).contentType(MediaType.APPLICATION_JSON)
            .content(cuerpo())).andExpect(status().isForbidden());
        mvc.perform(patch("/api/servicio/mis-publicaciones/" + propia.idServicio() + "/cancelar")
            .with(csrf()).with(user(otro.getIdUsuario().toString()))).andExpect(status().isForbidden());
        mvc.perform(put("/api/servicio/mis-publicaciones/" + propia.idServicio()).with(csrf())
            .with(user(empleador.getIdUsuario().toString())).contentType(MediaType.APPLICATION_JSON)
            .content(cuerpo().replace("Medellín", "Envigado")))
            .andExpect(status().isOk()).andExpect(jsonPath("$.ubicacion").value("Envigado"));
        mvc.perform(patch("/api/servicio/mis-publicaciones/" + propia.idServicio() + "/cancelar")
            .with(csrf()).with(user(empleador.getIdUsuario().toString())))
            .andExpect(status().isOk()).andExpect(jsonPath("$.estado.nombreEstado").value("Cancelado"));
        mvc.perform(put("/api/servicio/mis-publicaciones/" + propia.idServicio()).with(csrf())
            .with(user(empleador.getIdUsuario().toString())).contentType(MediaType.APPLICATION_JSON)
            .content(cuerpo())).andExpect(status().isConflict());
    }

    @Test
    void paginaYFiltrosSeResuelvenEnLaBase() throws Exception {
        for (int i = 0; i < 5; i++) servicios.publicar(datos("Medellín centro " + i), identidad(empleador));
        servicios.publicar(datos("Bogotá"), identidad(otro));
        var lector = user(trabajador.getIdUsuario().toString());
        mvc.perform(get("/api/servicio/buscar").with(lector).param("pagina", "1").param("tamano", "2"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.contenido.length()").value(2))
            .andExpect(jsonPath("$.totalElementos").value(6)).andExpect(jsonPath("$.totalPaginas").value(3));
        mvc.perform(get("/api/servicio/buscar").with(lector).param("ubicacion", "MEDELLÍN")
            .param("tipoId", tipo.getIdTipoServicio().toString())
            .param("fechaDesde", LocalDate.now().plusDays(10).toString())
            .param("fechaHasta", LocalDate.now().plusDays(10).toString()))
            .andExpect(status().isOk()).andExpect(jsonPath("$.totalElementos").value(5));
        var especial = servicios.publicar(datos("Sector 100%_seguro"), identidad(otro));
        mvc.perform(get("/api/servicio/buscar").with(lector).param("busqueda", "%_"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.totalElementos").value(1))
            .andExpect(jsonPath("$.contenido[0].idServicio").value(especial.idServicio()));
    }

    @Test
    void rechazaFechasHorariosImportesYParametrosInvalidos() throws Exception {
        var propietario = user(empleador.getIdUsuario().toString());
        for (String invalido : java.util.List.of(cuerpo().replace("12:00", "07:00"),
                cuerpo().replace(LocalDate.now().plusDays(10).toString(), LocalDate.now().minusDays(1).toString()),
                cuerpo().replace("100000", "-1"), cuerpo().replace("Medellín", "   "))) {
            mvc.perform(post("/api/servicio/mis-publicaciones").with(propietario).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(invalido)).andExpect(status().isBadRequest());
        }
        mvc.perform(get("/api/servicio/buscar").with(propietario).param("tamano", "51"))
            .andExpect(status().isBadRequest());
        mvc.perform(get("/api/servicio/buscar").with(propietario).param("pagina", "-1"))
            .andExpect(status().isBadRequest());
        mvc.perform(get("/api/servicio/buscar").with(propietario).param("fechaDesde", "2027-02-01")
            .param("fechaHasta", "2027-01-01")).andExpect(status().isBadRequest());
        mvc.perform(post("/api/servicio/mis-publicaciones").with(propietario).with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content(cuerpo().replace("\"idTipoServicio\":" + tipo.getIdTipoServicio(),
                "\"idTipoServicio\":999999"))).andExpect(status().isNotFound());
        assertEquals(0, ofertas.count());
    }

    @Test
    void noModificaOfertasConPostulacionOContratacionSinImplementarEsosFlujos() throws Exception {
        var oferta = servicios.publicar(datos("Medellín"), identidad(empleador));
        var perfil = trabajadores.findFirstByUsuario_IdUsuarioOrderByIdTrabajadorAsc(trabajador.getIdUsuario()).orElseThrow();
        Postulacion post = new Postulacion(); post.setServicio(ofertas.getReferenceById(oferta.idServicio()));
        post.setTrabajador(perfil); postulaciones.saveAndFlush(post);
        mvc.perform(put("/api/servicio/mis-publicaciones/" + oferta.idServicio()).with(csrf())
            .with(user(empleador.getIdUsuario().toString())).contentType(MediaType.APPLICATION_JSON)
            .content(cuerpo())).andExpect(status().isConflict());
        var segunda = servicios.publicar(datos("Envigado"), identidad(empleador));
        Contratacion contrato = new Contratacion(); contrato.setServicio(ofertas.getReferenceById(segunda.idServicio()));
        contrato.setTrabajador(perfil); contrataciones.saveAndFlush(contrato);
        mvc.perform(patch("/api/servicio/mis-publicaciones/" + segunda.idServicio() + "/cancelar")
            .with(csrf()).with(user(empleador.getIdUsuario().toString()))).andExpect(status().isConflict());
    }
}
