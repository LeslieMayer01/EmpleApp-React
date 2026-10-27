package proy.EmpleApp;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import proy.EmpleApp.modelo.*;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Stream;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PlantillasTests {
    @Autowired WebApplicationContext contexto;
    @Autowired EntityManager em;
    MockMvc mvc;
    Map<String, Integer> ids = new HashMap<>();

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).build();
        Rol rol = new Rol(); rol.setNombreRol("Trabajador"); em.persist(rol);
        Usuario usuario = new Usuario(); usuario.setNombre("Ana"); usuario.setCorreo("plantillas@example.com"); usuario.setContrasenaHash("hash_prueba"); usuario.setRol(rol); em.persist(usuario);
        Empleador empleador = new Empleador(); empleador.setUsuario(usuario); em.persist(empleador);
        Trabajador trabajador = new Trabajador(); trabajador.setUsuario(usuario); em.persist(trabajador);
        TipoServicio tipo = new TipoServicio(); tipo.setNombreTipo("Aseo"); em.persist(tipo);
        EstadoServicio estado = new EstadoServicio(); estado.setNombreEstado("Pendiente"); em.persist(estado);
        Servicio servicio = new Servicio(); servicio.setEmpleador(empleador); servicio.setTipoServicio(tipo); servicio.setEstado(estado); em.persist(servicio);
        Contratacion contratacion = new Contratacion(); contratacion.setServicio(servicio); contratacion.setTrabajador(trabajador); em.persist(contratacion);
        Pago pago = new Pago(); pago.setContratacion(contratacion); em.persist(pago);
        Postulacion postulacion = new Postulacion(); postulacion.setServicio(servicio); postulacion.setTrabajador(trabajador); em.persist(postulacion);
        DocumentoTrabajador documento = new DocumentoTrabajador(); documento.setTrabajador(trabajador); em.persist(documento);
        em.flush();
        ids.put("rol", rol.getIdRol()); ids.put("usuario", usuario.getIdUsuario());
        ids.put("empleador", empleador.getIdEmpleador()); ids.put("trabajador", trabajador.getIdTrabajador());
        ids.put("tipo-servicio", tipo.getIdTipoServicio()); ids.put("estado-servicio", estado.getIdEstado());
        ids.put("servicio", servicio.getIdServicio()); ids.put("contratacion", contratacion.getIdContratacion());
        ids.put("pago", pago.getIdPago()); ids.put("postulacion", postulacion.getIdPostulacion());
        ids.put("documento-trabajador", documento.getIdDocumento());
    }

    static Stream<String> recursos() { return Stream.of("contratacion", "documento-trabajador", "empleador", "estado-servicio", "pago", "postulacion", "rol", "servicio", "tipo-servicio", "trabajador", "usuario"); }

    @ParameterizedTest
    @MethodSource("recursos")
    void renderizaLasSeisVistasDeCadaEntidad(String recurso) throws Exception {
        for (String accion : new String[]{"registrar", "actualizar", "consultar", "consultar-id", "detalle", "eliminar"}) {
            mvc.perform(get("/" + recurso + "/" + accion).param("id", ids.get(recurso).toString()))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith("text/html"));
        }
        for (String accion : new String[]{"actualizar", "eliminar", "consultar-id"}) {
            mvc.perform(get("/" + recurso + "/" + accion)).andExpect(status().isOk());
        }
    }

    @Test
    void indexYFormularioConErrores() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("index"));
        mvc.perform(post("/rol/registrar").param("nombreRol", ""))
            .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("registro", "nombreRol"));
    }

    @Test
    void registraActualizaConsultaYEliminaRol() throws Exception {
        mvc.perform(post("/rol/registrar").param("nombreRol", "Supervisor"))
            .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/rol/consultar"));
        Integer id = em.createQuery("select r.idRol from Rol r where r.nombreRol = 'Supervisor'", Integer.class).getSingleResult();
        mvc.perform(post("/rol/actualizar").param("idRol", id.toString()).param("nombreRol", "Coordinador"))
            .andExpect(status().is3xxRedirection());
        em.flush(); em.clear();
        assertEquals("Coordinador", em.find(Rol.class, id).getNombreRol());
        mvc.perform(get("/rol/consultar-id").param("id", id.toString())).andExpect(view().name("rol-detalle"));
        mvc.perform(post("/rol/eliminar").param("id", id.toString())).andExpect(status().is3xxRedirection());
        em.flush(); em.clear(); assertNull(em.find(Rol.class, id));
    }

    @Test
    void registraUsuarioConRelacionYFechaHtml() throws Exception {
        mvc.perform(post("/usuario/registrar").param("nombre", "Luis").param("correo", "luis@example.com")
            .param("contrasenaHash", "hash_existente").param("rol.idRol", ids.get("rol").toString()))
            .andExpect(status().is3xxRedirection());
        mvc.perform(post("/postulacion/registrar").param("servicio.idServicio", ids.get("servicio").toString())
            .param("trabajador.idTrabajador", ids.get("trabajador").toString())
            .param("fechaPostulacion", "2026-10-26T14:30"))
            .andExpect(status().is3xxRedirection());
    }

    @Test
    void idInexistenteMuestraMensaje() throws Exception {
        mvc.perform(get("/rol/consultar-id").param("id", "2147483647"))
            .andExpect(status().isOk()).andExpect(model().attributeExists("error"));
    }
}
