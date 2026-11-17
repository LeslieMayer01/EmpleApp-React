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
import proy.EmpleApp.modelo.Postulacion;
import proy.EmpleApp.servicio.IPostulacionServicio;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import java.io.IOException;
import proy.EmpleApp.modelo.*;
import proy.EmpleApp.servicio.*;

/** Atiende las vistas Thymeleaf y las operaciones JSON de esta entidad. */
@Controller
@CrossOrigin(origins = "http://localhost:5173")
public class PostulacionControlador {

    @Autowired
    private IPostulacionServicio servicio;

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @GetMapping("/api/postulacion")
    public List<Postulacion> listar(Model modelo) {
        List<Postulacion> registros = servicio.listarPostulacion();
        modelo.addAttribute("registros", registros);
        return registros;
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @GetMapping("/api/postulacion/{id}")
    public Postulacion buscar(@PathVariable Integer id) {
        return servicio.buscarPostulacion(id);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @PostMapping("/api/postulacion")
    @ResponseStatus(HttpStatus.CREATED)
    public Postulacion crear(@Valid @RequestBody Postulacion entidad, BindingResult resultado) {
        validar(resultado);
        entidad.setIdPostulacion(null);
        return servicio.guardarPostulacion(entidad);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @PutMapping("/api/postulacion/{id}")
    public Postulacion actualizar(@PathVariable Integer id,
            @Valid @RequestBody Postulacion entidad, BindingResult resultado) {
        validar(resultado);
        entidad.setIdPostulacion(id);
        return servicio.guardarPostulacion(entidad);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @DeleteMapping("/api/postulacion/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) { servicio.eliminarPostulacion(id); }

    private void validar(BindingResult resultado) {
        if (resultado.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Los datos enviados no cumplen las validaciones");
        }
    }


    @Autowired
    private IServicioServicio opcionesServicio;
    @Autowired
    private ITrabajadorServicio opcionesTrabajador;

    // Limita los campos que el formulario puede asignar a la entidad y sus relaciones.
    @InitBinder("registro")
    public void configurarCampos(WebDataBinder binder) {
        binder.setAllowedFields("idPostulacion", "servicio.idServicio", "trabajador.idTrabajador", "fechaPostulacion");
    }

    // Proporciona al modelo las opciones de los selectores de relaciones, cuando existen.
    private void cargarOpciones(Model modelo) {
        modelo.addAttribute("opcionesServicio", opcionesServicio.listarServicio());
        modelo.addAttribute("opcionesTrabajador", opcionesTrabajador.listarTrabajador());
    }

    @GetMapping({"/postulacion", "/postulacion/consultar"})
    public String consultar(Model modelo) {
        // Thymeleaf obtiene esta colección para construir la tabla de consulta.
        modelo.addAttribute("registros", servicio.listarPostulacion());
        return "postulacion-consultar";
    }

    @GetMapping("/postulacion/registrar")
    public String registrar(Model modelo) {
        Postulacion registro = new Postulacion();
        registro.setServicio(new Servicio());
        registro.setTrabajador(new Trabajador());
        modelo.addAttribute("registro", registro);
        cargarOpciones(modelo);
        return "postulacion-registrar";
    }

    @GetMapping({"/postulacion/consultar-id", "/postulacion/detalle"})
    public String buscar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id == null) { return "postulacion-consultar-id"; }
        if (!cargarRegistro(id, modelo)) { return "postulacion-consultar-id"; }
        return "postulacion-detalle";
    }

    @GetMapping("/postulacion/actualizar")
    public String actualizar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id != null && cargarRegistro(id, modelo)) { cargarOpciones(modelo); }
        return "postulacion-actualizar";
    }

    @GetMapping("/postulacion/eliminar")
    public String eliminar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id != null) { cargarRegistro(id, modelo); }
        return "postulacion-eliminar";
    }

    // Carga el registro o prepara un mensaje visible si el ID no existe.
    private boolean cargarRegistro(Integer id, Model modelo) {
        try {
            modelo.addAttribute("registro", servicio.buscarPostulacion(id));
            return true;
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
            modelo.addAttribute("error", "No existe un registro con ese ID.");
            return false;
        }
    }

    @PostMapping({"/postulacion/registrar", "/postulacion/actualizar"})
    public String guardar(@Valid @ModelAttribute("registro") Postulacion registro,
            BindingResult resultado, @RequestParam(required = false) MultipartFile archivo,
            jakarta.servlet.http.HttpServletRequest solicitud, Model modelo, RedirectAttributes mensajes) {
        // El mismo método recibe los formularios de registro y de actualización.
        boolean actualizando = solicitud.getRequestURI().endsWith("/actualizar");
        if (!actualizando) { registro.setIdPostulacion(null); }
        if (actualizando && registro.getIdPostulacion() == null) {
            resultado.reject("id", "El ID es obligatorio para actualizar.");
        }
        // Solo guarda si la validación de la entidad y la conversión de campos fueron correctas.
        if (!resultado.hasErrors()) {
            try {
                servicio.guardarPostulacion(registro);
                // El mensaje flash se muestra una sola vez después de la redirección.
                mensajes.addFlashAttribute("mensaje", "Registro guardado correctamente.");
                return "redirect:/postulacion/consultar";
            // Informa los conflictos de unicidad o de relaciones que detecta la base de datos.
        } catch (DataIntegrityViolationException ex) {
                resultado.reject("integridad", "No se pudo guardar. Revisa los datos únicos y las relaciones.");
            } catch (ResponseStatusException ex) {
                if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
                resultado.reject("id", "El registro ya no existe.");
            }
        }
        cargarOpciones(modelo);
        return actualizando ? "postulacion-actualizar" : "postulacion-registrar";
    }

    @PostMapping("/postulacion/eliminar")
    public String confirmarEliminar(@RequestParam Integer id, RedirectAttributes mensajes) {
        try {
            servicio.eliminarPostulacion(id);
            mensajes.addFlashAttribute("mensaje", "Registro eliminado correctamente.");
        // Informa los conflictos de unicidad o de relaciones que detecta la base de datos.
        } catch (DataIntegrityViolationException ex) {
            mensajes.addFlashAttribute("error", "No se puede eliminar: hay registros relacionados.");
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
            mensajes.addFlashAttribute("error", "El registro ya no existe.");
        }
        return "redirect:/postulacion/consultar";
    }

}
