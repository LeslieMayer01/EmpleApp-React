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
import proy.EmpleApp.modelo.Rol;
import proy.EmpleApp.servicio.IRolServicio;
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
public class RolControlador {

    @Autowired
    private IRolServicio servicio;

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @GetMapping("/api/rol")
    public List<Rol> listar(Model modelo) {
        List<Rol> registros = servicio.listarRol();
        modelo.addAttribute("registros", registros);
        return registros;
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @GetMapping("/api/rol/{id}")
    public Rol buscar(@PathVariable Integer id) {
        return servicio.buscarRol(id);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @PostMapping("/api/rol")
    @ResponseStatus(HttpStatus.CREATED)
    public Rol crear(@Valid @RequestBody Rol entidad, BindingResult resultado) {
        validar(resultado);
        entidad.setIdRol(null);
        return servicio.guardarRol(entidad);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @PutMapping("/api/rol/{id}")
    public Rol actualizar(@PathVariable Integer id,
            @Valid @RequestBody Rol entidad, BindingResult resultado) {
        validar(resultado);
        entidad.setIdRol(id);
        return servicio.guardarRol(entidad);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @DeleteMapping("/api/rol/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) { servicio.eliminarRol(id); }

    private void validar(BindingResult resultado) {
        if (resultado.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Los datos enviados no cumplen las validaciones");
        }
    }



    // Limita los campos que el formulario puede asignar a la entidad y sus relaciones.
    @InitBinder("registro")
    public void configurarCampos(WebDataBinder binder) {
        binder.setAllowedFields("idRol", "nombreRol");
    }

    // Proporciona al modelo las opciones de los selectores de relaciones, cuando existen.
    private void cargarOpciones(Model modelo) {
    }

    @GetMapping({"/rol", "/rol/consultar"})
    public String consultar(Model modelo) {
        // Thymeleaf obtiene esta colección para construir la tabla de consulta.
        modelo.addAttribute("registros", servicio.listarRol());
        return "rol-consultar";
    }

    @GetMapping("/rol/registrar")
    public String registrar(Model modelo) {
        Rol registro = new Rol();
        modelo.addAttribute("registro", registro);
        cargarOpciones(modelo);
        return "rol-registrar";
    }

    @GetMapping({"/rol/consultar-id", "/rol/detalle"})
    public String buscar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id == null) { return "rol-consultar-id"; }
        if (!cargarRegistro(id, modelo)) { return "rol-consultar-id"; }
        return "rol-detalle";
    }

    @GetMapping("/rol/actualizar")
    public String actualizar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id != null && cargarRegistro(id, modelo)) { cargarOpciones(modelo); }
        return "rol-actualizar";
    }

    @GetMapping("/rol/eliminar")
    public String eliminar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id != null) { cargarRegistro(id, modelo); }
        return "rol-eliminar";
    }

    // Carga el registro o prepara un mensaje visible si el ID no existe.
    private boolean cargarRegistro(Integer id, Model modelo) {
        try {
            modelo.addAttribute("registro", servicio.buscarRol(id));
            return true;
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
            modelo.addAttribute("error", "No existe un registro con ese ID.");
            return false;
        }
    }

    @PostMapping({"/rol/registrar", "/rol/actualizar"})
    public String guardar(@Valid @ModelAttribute("registro") Rol registro,
            BindingResult resultado, @RequestParam(required = false) MultipartFile archivo,
            jakarta.servlet.http.HttpServletRequest solicitud, Model modelo, RedirectAttributes mensajes) {
        // El mismo método recibe los formularios de registro y de actualización.
        boolean actualizando = solicitud.getRequestURI().endsWith("/actualizar");
        if (!actualizando) { registro.setIdRol(null); }
        if (actualizando && registro.getIdRol() == null) {
            resultado.reject("id", "El ID es obligatorio para actualizar.");
        }
        // Solo guarda si la validación de la entidad y la conversión de campos fueron correctas.
        if (!resultado.hasErrors()) {
            try {
                servicio.guardarRol(registro);
                // El mensaje flash se muestra una sola vez después de la redirección.
                mensajes.addFlashAttribute("mensaje", "Registro guardado correctamente.");
                return "redirect:/rol/consultar";
            // Informa los conflictos de unicidad o de relaciones que detecta la base de datos.
        } catch (DataIntegrityViolationException ex) {
                resultado.reject("integridad", "No se pudo guardar. Revisa los datos únicos y las relaciones.");
            } catch (ResponseStatusException ex) {
                if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
                resultado.reject("id", "El registro ya no existe.");
            }
        }
        cargarOpciones(modelo);
        return actualizando ? "rol-actualizar" : "rol-registrar";
    }

    @PostMapping("/rol/eliminar")
    public String confirmarEliminar(@RequestParam Integer id, RedirectAttributes mensajes) {
        try {
            servicio.eliminarRol(id);
            mensajes.addFlashAttribute("mensaje", "Registro eliminado correctamente.");
        // Informa los conflictos de unicidad o de relaciones que detecta la base de datos.
        } catch (DataIntegrityViolationException ex) {
            mensajes.addFlashAttribute("error", "No se puede eliminar: hay registros relacionados.");
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
            mensajes.addFlashAttribute("error", "El registro ya no existe.");
        }
        return "redirect:/rol/consultar";
    }

}
