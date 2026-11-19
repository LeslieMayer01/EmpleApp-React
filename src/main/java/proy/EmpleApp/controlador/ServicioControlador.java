package proy.EmpleApp.controlador;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import proy.EmpleApp.modelo.Servicio;
import proy.EmpleApp.servicio.IServicioServicio;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import java.io.IOException;
import proy.EmpleApp.modelo.*;
import proy.EmpleApp.servicio.*;

/** Atiende las vistas Thymeleaf y las operaciones JSON de esta entidad. */
@Controller
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ServicioControlador {

    @Autowired
    private IServicioServicio servicio;

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @GetMapping("/api/servicio")
    public List<proy.EmpleApp.dto.ServicioResumen> listar(Model modelo) {
        List<Servicio> registros = servicio.listarServicio();
        modelo.addAttribute("registros", registros);
        return registros.stream().map(proy.EmpleApp.dto.ServicioResumen::de).toList();
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @GetMapping("/api/servicio/{id}")
    public proy.EmpleApp.dto.ServicioResumen buscar(@PathVariable Integer id) {
        return proy.EmpleApp.dto.ServicioResumen.de(servicio.buscarServicio(id));
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @PostMapping("/api/servicio")
    @ResponseStatus(HttpStatus.CREATED)
    public Servicio crear(@Valid @RequestBody Servicio entidad, BindingResult resultado) {
        validar(resultado);
        entidad.setIdServicio(null);
        return servicio.guardarServicio(entidad);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @PutMapping("/api/servicio/{id}")
    public Servicio actualizar(@PathVariable Integer id,
            @Valid @RequestBody Servicio entidad, BindingResult resultado) {
        validar(resultado);
        entidad.setIdServicio(id);
        return servicio.guardarServicio(entidad);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @DeleteMapping("/api/servicio/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) { servicio.eliminarServicio(id); }

    private void validar(BindingResult resultado) {
        if (resultado.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Los datos enviados no cumplen las validaciones");
        }
    }


    @Autowired
    private IEmpleadorServicio opcionesEmpleador;
    @Autowired
    private ITipoServicioServicio opcionesTipoServicio;
    @Autowired
    private IEstadoServicioServicio opcionesEstadoServicio;

    // Limita los campos que el formulario puede asignar a la entidad y sus relaciones.
    @InitBinder("registro")
    public void configurarCampos(WebDataBinder binder) {
        binder.setAllowedFields("idServicio", "empleador.idEmpleador", "tipoServicio.idTipoServicio", "estado.idEstado", "fecha", "horaInicio", "horaFin", "ubicacion", "valorOfertado");
    }

    // Proporciona al modelo las opciones de los selectores de relaciones, cuando existen.
    private void cargarOpciones(Model modelo) {
        modelo.addAttribute("opcionesEmpleador", opcionesEmpleador.listarEmpleador());
        modelo.addAttribute("opcionesTipoServicio", opcionesTipoServicio.listarTipoServicio());
        modelo.addAttribute("opcionesEstadoServicio", opcionesEstadoServicio.listarEstadoServicio());
    }

    @GetMapping({"/servicio", "/servicio/consultar"})
    public String consultar(Model modelo) {
        // Thymeleaf obtiene esta colección para construir la tabla de consulta.
        modelo.addAttribute("registros", servicio.listarServicio());
        return "servicio-consultar";
    }

    @GetMapping("/servicio/registrar")
    public String registrar(Model modelo) {
        Servicio registro = new Servicio();
        registro.setEmpleador(new Empleador());
        registro.setTipoServicio(new TipoServicio());
        registro.setEstado(new EstadoServicio());
        modelo.addAttribute("registro", registro);
        cargarOpciones(modelo);
        return "servicio-registrar";
    }

    @GetMapping({"/servicio/consultar-id", "/servicio/detalle"})
    public String buscar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id == null) { return "servicio-consultar-id"; }
        if (!cargarRegistro(id, modelo)) { return "servicio-consultar-id"; }
        return "servicio-detalle";
    }

    @GetMapping("/servicio/actualizar")
    public String actualizar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id != null && cargarRegistro(id, modelo)) { cargarOpciones(modelo); }
        return "servicio-actualizar";
    }

    @GetMapping("/servicio/eliminar")
    public String eliminar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id != null) { cargarRegistro(id, modelo); }
        return "servicio-eliminar";
    }

    // Carga el registro o prepara un mensaje visible si el ID no existe.
    private boolean cargarRegistro(Integer id, Model modelo) {
        try {
            modelo.addAttribute("registro", servicio.buscarServicio(id));
            return true;
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
            modelo.addAttribute("error", "No existe un registro con ese ID.");
            return false;
        }
    }

    @PostMapping({"/servicio/registrar", "/servicio/actualizar"})
    public String guardar(@Valid @ModelAttribute("registro") Servicio registro,
            BindingResult resultado, @RequestParam(required = false) MultipartFile archivo,
            jakarta.servlet.http.HttpServletRequest solicitud, Model modelo, RedirectAttributes mensajes) {
        // El mismo método recibe los formularios de registro y de actualización.
        boolean actualizando = solicitud.getRequestURI().endsWith("/actualizar");
        if (!actualizando) { registro.setIdServicio(null); }
        if (actualizando && registro.getIdServicio() == null) {
            resultado.reject("id", "El ID es obligatorio para actualizar.");
        }
        // Solo guarda si la validación de la entidad y la conversión de campos fueron correctas.
        if (!resultado.hasErrors()) {
            try {
                servicio.guardarServicio(registro);
                // El mensaje flash se muestra una sola vez después de la redirección.
                mensajes.addFlashAttribute("mensaje", "Registro guardado correctamente.");
                return "redirect:/servicio/consultar";
            // Informa los conflictos de unicidad o de relaciones que detecta la base de datos.
        } catch (DataIntegrityViolationException ex) {
                resultado.reject("integridad", "No se pudo guardar. Revisa los datos únicos y las relaciones.");
            } catch (ResponseStatusException ex) {
                if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
                resultado.reject("id", "El registro ya no existe.");
            }
        }
        cargarOpciones(modelo);
        return actualizando ? "servicio-actualizar" : "servicio-registrar";
    }

    @PostMapping("/servicio/eliminar")
    public String confirmarEliminar(@RequestParam Integer id, RedirectAttributes mensajes) {
        try {
            servicio.eliminarServicio(id);
            mensajes.addFlashAttribute("mensaje", "Registro eliminado correctamente.");
        // Informa los conflictos de unicidad o de relaciones que detecta la base de datos.
        } catch (DataIntegrityViolationException ex) {
            mensajes.addFlashAttribute("error", "No se puede eliminar: hay registros relacionados.");
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
            mensajes.addFlashAttribute("error", "El registro ya no existe.");
        }
        return "redirect:/servicio/consultar";
    }

}
