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
import proy.EmpleApp.modelo.Empleador;
import proy.EmpleApp.servicio.IEmpleadorServicio;
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
public class EmpleadorControlador {

    @Autowired
    private IEmpleadorServicio servicio;

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @GetMapping("/api/empleador")
    public List<Empleador> listar(Model modelo) {
        List<Empleador> registros = servicio.listarEmpleador();
        modelo.addAttribute("registros", registros);
        return registros;
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @GetMapping("/api/empleador/{id}")
    public Empleador buscar(@PathVariable Integer id) {
        return servicio.buscarEmpleador(id);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @PostMapping("/api/empleador")
    @ResponseStatus(HttpStatus.CREATED)
    public Empleador crear(@Valid @RequestBody Empleador entidad, BindingResult resultado) {
        validar(resultado);
        entidad.setIdEmpleador(null);
        return servicio.guardarEmpleador(entidad);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @PutMapping("/api/empleador/{id}")
    public Empleador actualizar(@PathVariable Integer id,
            @Valid @RequestBody Empleador entidad, BindingResult resultado) {
        validar(resultado);
        entidad.setIdEmpleador(id);
        return servicio.guardarEmpleador(entidad);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @DeleteMapping("/api/empleador/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) { servicio.eliminarEmpleador(id); }

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
        binder.setAllowedFields("idEmpleador", "usuario.idUsuario", "direccion", "telefono");
    }

    // Proporciona al modelo las opciones de los selectores de relaciones, cuando existen.
    private void cargarOpciones(Model modelo) {
        modelo.addAttribute("opcionesUsuario", opcionesUsuario.listarUsuario());
    }

    @GetMapping({"/empleador", "/empleador/consultar"})
    public String consultar(Model modelo) {
        // Thymeleaf obtiene esta colección para construir la tabla de consulta.
        modelo.addAttribute("registros", servicio.listarEmpleador());
        return "empleador-consultar";
    }

    @GetMapping("/empleador/registrar")
    public String registrar(Model modelo) {
        Empleador registro = new Empleador();
        registro.setUsuario(new Usuario());
        modelo.addAttribute("registro", registro);
        cargarOpciones(modelo);
        return "empleador-registrar";
    }

    @GetMapping({"/empleador/consultar-id", "/empleador/detalle"})
    public String buscar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id == null) { return "empleador-consultar-id"; }
        if (!cargarRegistro(id, modelo)) { return "empleador-consultar-id"; }
        return "empleador-detalle";
    }

    @GetMapping("/empleador/actualizar")
    public String actualizar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id != null && cargarRegistro(id, modelo)) { cargarOpciones(modelo); }
        return "empleador-actualizar";
    }

    @GetMapping("/empleador/eliminar")
    public String eliminar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id != null) { cargarRegistro(id, modelo); }
        return "empleador-eliminar";
    }

    // Carga el registro o prepara un mensaje visible si el ID no existe.
    private boolean cargarRegistro(Integer id, Model modelo) {
        try {
            modelo.addAttribute("registro", servicio.buscarEmpleador(id));
            return true;
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
            modelo.addAttribute("error", "No existe un registro con ese ID.");
            return false;
        }
    }

    @PostMapping({"/empleador/registrar", "/empleador/actualizar"})
    public String guardar(@Valid @ModelAttribute("registro") Empleador registro,
            BindingResult resultado, @RequestParam(required = false) MultipartFile archivo,
            jakarta.servlet.http.HttpServletRequest solicitud, Model modelo, RedirectAttributes mensajes) {
        // El mismo método recibe los formularios de registro y de actualización.
        boolean actualizando = solicitud.getRequestURI().endsWith("/actualizar");
        if (!actualizando) { registro.setIdEmpleador(null); }
        if (actualizando && registro.getIdEmpleador() == null) {
            resultado.reject("id", "El ID es obligatorio para actualizar.");
        }
        // Solo guarda si la validación de la entidad y la conversión de campos fueron correctas.
        if (!resultado.hasErrors()) {
            try {
                servicio.guardarEmpleador(registro);
                // El mensaje flash se muestra una sola vez después de la redirección.
                mensajes.addFlashAttribute("mensaje", "Registro guardado correctamente.");
                return "redirect:/empleador/consultar";
            // Informa los conflictos de unicidad o de relaciones que detecta la base de datos.
        } catch (DataIntegrityViolationException ex) {
                resultado.reject("integridad", "No se pudo guardar. Revisa los datos únicos y las relaciones.");
            } catch (ResponseStatusException ex) {
                if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
                resultado.reject("id", "El registro ya no existe.");
            }
        }
        cargarOpciones(modelo);
        return actualizando ? "empleador-actualizar" : "empleador-registrar";
    }

    @PostMapping("/empleador/eliminar")
    public String confirmarEliminar(@RequestParam Integer id, RedirectAttributes mensajes) {
        try {
            servicio.eliminarEmpleador(id);
            mensajes.addFlashAttribute("mensaje", "Registro eliminado correctamente.");
        // Informa los conflictos de unicidad o de relaciones que detecta la base de datos.
        } catch (DataIntegrityViolationException ex) {
            mensajes.addFlashAttribute("error", "No se puede eliminar: hay registros relacionados.");
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
            mensajes.addFlashAttribute("error", "El registro ya no existe.");
        }
        return "redirect:/empleador/consultar";
    }

}
