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
import proy.EmpleApp.modelo.Trabajador;
import proy.EmpleApp.servicio.ITrabajadorServicio;
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
public class TrabajadorControlador {

    @Autowired
    private ITrabajadorServicio servicio;

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @GetMapping("/api/trabajador")
    public List<Trabajador> listar(Model modelo) {
        List<Trabajador> registros = servicio.listarTrabajador();
        modelo.addAttribute("registros", registros);
        return registros;
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @GetMapping("/api/trabajador/{id}")
    public Trabajador buscar(@PathVariable Integer id) {
        return servicio.buscarTrabajador(id);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @PostMapping("/api/trabajador")
    @ResponseStatus(HttpStatus.CREATED)
    public Trabajador crear(@Valid @RequestBody Trabajador entidad, BindingResult resultado) {
        validar(resultado);
        entidad.setIdTrabajador(null);
        return servicio.guardarTrabajador(entidad);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @PutMapping("/api/trabajador/{id}")
    public Trabajador actualizar(@PathVariable Integer id,
            @Valid @RequestBody Trabajador entidad, BindingResult resultado) {
        validar(resultado);
        entidad.setIdTrabajador(id);
        return servicio.guardarTrabajador(entidad);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @DeleteMapping("/api/trabajador/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) { servicio.eliminarTrabajador(id); }

    private void validar(BindingResult resultado) {
        if (resultado.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Los datos enviados no cumplen las validaciones");
        }
    }


    @Autowired
    private IUsuarioServicio opcionesUsuario;

    // Limita los campos que el formulario puede asignar a la entidad y sus relaciones.
    @InitBinder("registro")
    public void configurarCampos(WebDataBinder binder) {
        binder.setAllowedFields("idTrabajador", "usuario.idUsuario", "experiencia", "disponibilidad");
    }

    // Proporciona al modelo las opciones de los selectores de relaciones, cuando existen.
    private void cargarOpciones(Model modelo) {
        modelo.addAttribute("opcionesUsuario", opcionesUsuario.listarUsuario());
    }

    @GetMapping({"/trabajador", "/trabajador/consultar"})
    public String consultar(Model modelo) {
        // Thymeleaf obtiene esta colección para construir la tabla de consulta.
        modelo.addAttribute("registros", servicio.listarTrabajador());
        return "trabajador-consultar";
    }

    @GetMapping("/trabajador/registrar")
    public String registrar(Model modelo) {
        Trabajador registro = new Trabajador();
        registro.setUsuario(new Usuario());
        modelo.addAttribute("registro", registro);
        cargarOpciones(modelo);
        return "trabajador-registrar";
    }

    @GetMapping({"/trabajador/consultar-id", "/trabajador/detalle"})
    public String buscar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id == null) { return "trabajador-consultar-id"; }
        if (!cargarRegistro(id, modelo)) { return "trabajador-consultar-id"; }
        return "trabajador-detalle";
    }

    @GetMapping("/trabajador/actualizar")
    public String actualizar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id != null && cargarRegistro(id, modelo)) { cargarOpciones(modelo); }
        return "trabajador-actualizar";
    }

    @GetMapping("/trabajador/eliminar")
    public String eliminar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id != null) { cargarRegistro(id, modelo); }
        return "trabajador-eliminar";
    }

    // Carga el registro o prepara un mensaje visible si el ID no existe.
    private boolean cargarRegistro(Integer id, Model modelo) {
        try {
            modelo.addAttribute("registro", servicio.buscarTrabajador(id));
            return true;
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
            modelo.addAttribute("error", "No existe un registro con ese ID.");
            return false;
        }
    }

    @PostMapping({"/trabajador/registrar", "/trabajador/actualizar"})
    public String guardar(@Valid @ModelAttribute("registro") Trabajador registro,
            BindingResult resultado, @RequestParam(required = false) MultipartFile archivo,
            jakarta.servlet.http.HttpServletRequest solicitud, Model modelo, RedirectAttributes mensajes) {
        // El mismo método recibe los formularios de registro y de actualización.
        boolean actualizando = solicitud.getRequestURI().endsWith("/actualizar");
        if (!actualizando) { registro.setIdTrabajador(null); }
        if (actualizando && registro.getIdTrabajador() == null) {
            resultado.reject("id", "El ID es obligatorio para actualizar.");
        }
        // Solo guarda si la validación de la entidad y la conversión de campos fueron correctas.
        if (!resultado.hasErrors()) {
            try {
                servicio.guardarTrabajador(registro);
                // El mensaje flash se muestra una sola vez después de la redirección.
                mensajes.addFlashAttribute("mensaje", "Registro guardado correctamente.");
                return "redirect:/trabajador/consultar";
            // Informa los conflictos de unicidad o de relaciones que detecta la base de datos.
        } catch (DataIntegrityViolationException ex) {
                resultado.reject("integridad", "No se pudo guardar. Revisa los datos únicos y las relaciones.");
            } catch (ResponseStatusException ex) {
                if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
                resultado.reject("id", "El registro ya no existe.");
            }
        }
        cargarOpciones(modelo);
        return actualizando ? "trabajador-actualizar" : "trabajador-registrar";
    }

    @PostMapping("/trabajador/eliminar")
    public String confirmarEliminar(@RequestParam Integer id, RedirectAttributes mensajes) {
        try {
            servicio.eliminarTrabajador(id);
            mensajes.addFlashAttribute("mensaje", "Registro eliminado correctamente.");
        // Informa los conflictos de unicidad o de relaciones que detecta la base de datos.
        } catch (DataIntegrityViolationException ex) {
            mensajes.addFlashAttribute("error", "No se puede eliminar: hay registros relacionados.");
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
            mensajes.addFlashAttribute("error", "El registro ya no existe.");
        }
        return "redirect:/trabajador/consultar";
    }

}
