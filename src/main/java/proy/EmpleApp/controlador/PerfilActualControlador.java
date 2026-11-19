package proy.EmpleApp.controlador;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import proy.EmpleApp.modelo.Empleador;
import proy.EmpleApp.modelo.Trabajador;
import proy.EmpleApp.repositorio.IEmpleadorRepositorio;
import proy.EmpleApp.repositorio.ITrabajadorRepositorio;
import proy.EmpleApp.seguridad.UsuarioActual;

/** El propietario se deriva de la sesión; los formularios no pueden cambiar su ID ni su rol. */
@Controller
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PerfilActualControlador {
    private final UsuarioActual actual;
    private final ITrabajadorRepositorio trabajadores;
    private final IEmpleadorRepositorio empleadores;

    public PerfilActualControlador(UsuarioActual actual, ITrabajadorRepositorio trabajadores,
            IEmpleadorRepositorio empleadores) {
        this.actual = actual;
        this.trabajadores = trabajadores;
        this.empleadores = empleadores;
    }

    public record DatosTrabajador(String experiencia, @Size(max = 100) String disponibilidad) { }
    public record DatosEmpleador(@Size(max = 255) String direccion, @Size(max = 20) String telefono) { }

    @ResponseBody
    @GetMapping("/api/trabajador/me")
    public Trabajador trabajador(Authentication autenticacion) {
        Integer id = actual.exigirRol(autenticacion, "Trabajador").getIdUsuario();
        return trabajadores.findFirstByUsuario_IdUsuarioOrderByIdTrabajadorAsc(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "No tienes un perfil de trabajador"));
    }

    @ResponseBody
    @PutMapping("/api/trabajador/me")
    @Transactional
    public Trabajador actualizarTrabajador(Authentication autenticacion,
            @Valid @RequestBody DatosTrabajador datos) {
        Trabajador perfil = trabajador(autenticacion);
        perfil.setExperiencia(datos.experiencia());
        perfil.setDisponibilidad(datos.disponibilidad());
        return trabajadores.save(perfil);
    }

    @ResponseBody
    @GetMapping("/api/empleador/me")
    public Empleador empleador(Authentication autenticacion) {
        Integer id = actual.exigirRol(autenticacion, "Empleador").getIdUsuario();
        return empleadores.findFirstByUsuario_IdUsuarioOrderByIdEmpleadorAsc(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "No tienes un perfil de empleador"));
    }

    @ResponseBody
    @PutMapping("/api/empleador/me")
    @Transactional
    public Empleador actualizarEmpleador(Authentication autenticacion,
            @Valid @RequestBody DatosEmpleador datos) {
        Empleador perfil = empleador(autenticacion);
        perfil.setDireccion(datos.direccion());
        perfil.setTelefono(datos.telefono());
        return empleadores.save(perfil);
    }
}
