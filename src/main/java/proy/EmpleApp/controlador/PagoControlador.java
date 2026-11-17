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
import proy.EmpleApp.modelo.Pago;
import proy.EmpleApp.servicio.IPagoServicio;
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
public class PagoControlador {

    @Autowired
    private IPagoServicio servicio;

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @GetMapping("/api/pago")
    public List<Pago> listar(Model modelo) {
        List<Pago> registros = servicio.listarPago();
        modelo.addAttribute("registros", registros);
        return registros;
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @GetMapping("/api/pago/{id}")
    public Pago buscar(@PathVariable Integer id) {
        return servicio.buscarPago(id);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @PostMapping("/api/pago")
    @ResponseStatus(HttpStatus.CREATED)
    public Pago crear(@Valid @RequestBody Pago entidad, BindingResult resultado) {
        validar(resultado);
        entidad.setIdPago(null);
        return servicio.guardarPago(entidad);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @PutMapping("/api/pago/{id}")
    public Pago actualizar(@PathVariable Integer id,
            @Valid @RequestBody Pago entidad, BindingResult resultado) {
        validar(resultado);
        entidad.setIdPago(id);
        return servicio.guardarPago(entidad);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @DeleteMapping("/api/pago/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) { servicio.eliminarPago(id); }

    private void validar(BindingResult resultado) {
        if (resultado.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Los datos enviados no cumplen las validaciones");
        }
    }


    @Autowired
    private IContratacionServicio opcionesContratacion;

    // Limita los campos que el formulario puede asignar a la entidad y sus relaciones.
    @InitBinder("registro")
    public void configurarCampos(WebDataBinder binder) {
        binder.setAllowedFields("idPago", "contratacion.idContratacion", "valorServicio", "fechaPago");
    }

    // Proporciona al modelo las opciones de los selectores de relaciones, cuando existen.
    private void cargarOpciones(Model modelo) {
        modelo.addAttribute("opcionesContratacion", opcionesContratacion.listarContratacion());
    }

    @GetMapping({"/pago", "/pago/consultar"})
    public String consultar(Model modelo) {
        // Thymeleaf obtiene esta colección para construir la tabla de consulta.
        modelo.addAttribute("registros", servicio.listarPago());
        return "pago-consultar";
    }

    @GetMapping("/pago/registrar")
    public String registrar(Model modelo) {
        Pago registro = new Pago();
        registro.setContratacion(new Contratacion());
        modelo.addAttribute("registro", registro);
        cargarOpciones(modelo);
        return "pago-registrar";
    }

    @GetMapping({"/pago/consultar-id", "/pago/detalle"})
    public String buscar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id == null) { return "pago-consultar-id"; }
        if (!cargarRegistro(id, modelo)) { return "pago-consultar-id"; }
        return "pago-detalle";
    }

    @GetMapping("/pago/actualizar")
    public String actualizar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id != null && cargarRegistro(id, modelo)) { cargarOpciones(modelo); }
        return "pago-actualizar";
    }

    @GetMapping("/pago/eliminar")
    public String eliminar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id != null) { cargarRegistro(id, modelo); }
        return "pago-eliminar";
    }

    // Carga el registro o prepara un mensaje visible si el ID no existe.
    private boolean cargarRegistro(Integer id, Model modelo) {
        try {
            modelo.addAttribute("registro", servicio.buscarPago(id));
            return true;
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
            modelo.addAttribute("error", "No existe un registro con ese ID.");
            return false;
        }
    }

    @PostMapping({"/pago/registrar", "/pago/actualizar"})
    public String guardar(@Valid @ModelAttribute("registro") Pago registro,
            BindingResult resultado, @RequestParam(required = false) MultipartFile archivo,
            jakarta.servlet.http.HttpServletRequest solicitud, Model modelo, RedirectAttributes mensajes) {
        // El mismo método recibe los formularios de registro y de actualización.
        boolean actualizando = solicitud.getRequestURI().endsWith("/actualizar");
        if (!actualizando) { registro.setIdPago(null); }
        if (actualizando && registro.getIdPago() == null) {
            resultado.reject("id", "El ID es obligatorio para actualizar.");
        }
        // Solo guarda si la validación de la entidad y la conversión de campos fueron correctas.
        if (!resultado.hasErrors()) {
            try {
                servicio.guardarPago(registro);
                // El mensaje flash se muestra una sola vez después de la redirección.
                mensajes.addFlashAttribute("mensaje", "Registro guardado correctamente.");
                return "redirect:/pago/consultar";
            // Informa los conflictos de unicidad o de relaciones que detecta la base de datos.
        } catch (DataIntegrityViolationException ex) {
                resultado.reject("integridad", "No se pudo guardar. Revisa los datos únicos y las relaciones.");
            } catch (ResponseStatusException ex) {
                if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
                resultado.reject("id", "El registro ya no existe.");
            }
        }
        cargarOpciones(modelo);
        return actualizando ? "pago-actualizar" : "pago-registrar";
    }

    @PostMapping("/pago/eliminar")
    public String confirmarEliminar(@RequestParam Integer id, RedirectAttributes mensajes) {
        try {
            servicio.eliminarPago(id);
            mensajes.addFlashAttribute("mensaje", "Registro eliminado correctamente.");
        // Informa los conflictos de unicidad o de relaciones que detecta la base de datos.
        } catch (DataIntegrityViolationException ex) {
            mensajes.addFlashAttribute("error", "No se puede eliminar: hay registros relacionados.");
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
            mensajes.addFlashAttribute("error", "El registro ya no existe.");
        }
        return "redirect:/pago/consultar";
    }

}
