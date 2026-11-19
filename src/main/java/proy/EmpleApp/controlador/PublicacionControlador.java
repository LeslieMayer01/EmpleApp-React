package proy.EmpleApp.controlador;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import proy.EmpleApp.dto.*;
import proy.EmpleApp.servicio.IServicioServicio;

/** Operaciones del catálogo y del empleador, separadas del CRUD administrativo existente. */
@Controller
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PublicacionControlador {
    private final IServicioServicio servicios;

    public PublicacionControlador(IServicioServicio servicios) { this.servicios = servicios; }

    @ResponseBody
    @GetMapping("/api/servicio/buscar")
    public PaginaServicios buscar(@Valid @ModelAttribute FiltroServicios filtro, Authentication autenticacion) {
        return servicios.buscarPublicaciones(filtro, autenticacion, false);
    }

    @ResponseBody
    @GetMapping("/api/servicio/mis-publicaciones")
    public PaginaServicios propias(@Valid @ModelAttribute FiltroServicios filtro, Authentication autenticacion) {
        return servicios.buscarPublicaciones(filtro, autenticacion, true);
    }

    @ResponseBody
    @PostMapping("/api/servicio/mis-publicaciones")
    @ResponseStatus(HttpStatus.CREATED)
    public ServicioResumen publicar(@Valid @RequestBody DatosPublicacion datos, Authentication autenticacion) {
        return servicios.publicar(datos, autenticacion);
    }

    @ResponseBody
    @PutMapping("/api/servicio/mis-publicaciones/{id}")
    public ServicioResumen actualizar(@PathVariable Integer id, @Valid @RequestBody DatosPublicacion datos,
            Authentication autenticacion) {
        return servicios.actualizarPublicacion(id, datos, autenticacion);
    }

    @ResponseBody
    @PatchMapping("/api/servicio/mis-publicaciones/{id}/cancelar")
    public ServicioResumen cancelar(@PathVariable Integer id, Authentication autenticacion) {
        return servicios.cancelarPublicacion(id, autenticacion);
    }
}
