package proy.EmpleApp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import proy.EmpleApp.modelo.Rol;
import proy.EmpleApp.modelo.Usuario;
import proy.EmpleApp.repositorio.IRolRepositorio;
import proy.EmpleApp.repositorio.IUsuarioRepositorio;
import jakarta.persistence.EntityManager;
import jakarta.validation.Validator;
import proy.EmpleApp.controlador.RolControlador;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmpleAppApplicationTests {
    @Autowired IRolRepositorio roles;
    @Autowired IUsuarioRepositorio usuarios;
    @Autowired EntityManager entityManager;
    @Autowired Validator validator;
    @Autowired RolControlador rolControlador;
    @Autowired proy.EmpleApp.controlador.UsuarioControlador usuarioControlador;
    @Autowired proy.EmpleApp.servicio.IUsuarioServicio servicioUsuario;

    @Test
    @Transactional
    void loginValidaCredencialesSinExponerContrasena() throws Exception {
        Rol rol = new Rol();
        rol.setNombreRol("Login prueba");
        roles.saveAndFlush(rol);
        Usuario usuario = new Usuario();
        usuario.setNombre("Ana Login");
        usuario.setCorreo("login@example.com");
        usuario.setContrasenaHash("ClaveSoloDePrueba123");
        usuario.setRol(rol);
        servicioUsuario.guardarUsuario(usuario);
        usuarios.flush();
        assertNotEquals("ClaveSoloDePrueba123", usuario.getContrasenaHash());
        String hash = usuario.getContrasenaHash();
        servicioUsuario.guardarUsuario(usuario);
        assertEquals(hash, usuario.getContrasenaHash());

        var mvc = MockMvcBuilders.standaloneSetup(usuarioControlador).build();
        mvc.perform(post("/api/usuario/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"correo\":\"LOGIN@example.com\",\"contrasena\":\"ClaveSoloDePrueba123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.correo").value("login@example.com"))
            .andExpect(jsonPath("$.contrasenaHash").doesNotExist());
        mvc.perform(post("/api/usuario/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"correo\":\"login@example.com\",\"contrasena\":\"incorrecta\"}"))
            .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/usuario/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"correo\":\"ausente@example.com\",\"contrasena\":\"incorrecta\"}"))
            .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/usuario/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"correo\":\"login@example.com\"}"))
            .andExpect(status().isBadRequest());
    }

	@Test
	void contextLoads() {
	}

    @Test
    @Transactional
    void guardaUsuarioConRolYRecuperaRelacion() {
        Rol rol = new Rol();
        rol.setNombreRol("Trabajador");
        roles.saveAndFlush(rol);

        Usuario usuario = new Usuario();
        usuario.setNombre("Ana");
        usuario.setCorreo("ana@example.com");
        usuario.setContrasenaHash("hash_de_prueba");
        usuario.setRol(rol);
        usuarios.saveAndFlush(usuario);
        Integer id = usuario.getIdUsuario();
        assertNotNull(id);
        entityManager.clear();

        Usuario recuperado = usuarios.findById(id).orElseThrow();
        assertEquals("Trabajador", recuperado.getRol().getNombreRol());
        assertEquals("hash_de_prueba", recuperado.getContrasenaHash());
    }

    @Test
    void rechazaCamposObligatoriosYCorreoInvalido() {
        Usuario usuario = new Usuario();
        usuario.setCorreo("correo-invalido");
        var errores = validator.validate(usuario);
        assertTrue(errores.stream().anyMatch(e -> e.getPropertyPath().toString().equals("correo")));
        assertTrue(errores.stream().anyMatch(e -> e.getPropertyPath().toString().equals("nombre")));
        assertTrue(errores.stream().anyMatch(e -> e.getPropertyPath().toString().equals("rol")));
    }

    @Test
    @Transactional
    void controladorDevuelveJsonYRechazaDatosInvalidos() throws Exception {
        var mvc = MockMvcBuilders.standaloneSetup(rolControlador).build();
        mvc.perform(post("/api/rol").contentType(MediaType.APPLICATION_JSON)
            .content("{\"nombreRol\":\"Supervisor\"}"))
            .andExpect(status().isCreated())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        mvc.perform(get("/api/rol"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        mvc.perform(post("/api/rol").contentType(MediaType.APPLICATION_JSON)
            .content("{\"nombreRol\":\"\"}"))
            .andExpect(status().isBadRequest());
    }

}
