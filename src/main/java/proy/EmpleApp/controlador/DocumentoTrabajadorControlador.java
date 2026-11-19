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
import proy.EmpleApp.modelo.DocumentoTrabajador;
import proy.EmpleApp.servicio.IDocumentoTrabajadorServicio;
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
public class DocumentoTrabajadorControlador {

    @Autowired
    private IDocumentoTrabajadorServicio servicio;

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @GetMapping("/api/documentotrabajador")
    public List<DocumentoTrabajador> listar(Model modelo) {
        List<DocumentoTrabajador> registros = servicio.listarDocumentoTrabajador();
        modelo.addAttribute("registros", registros);
        return registros;
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @GetMapping("/api/documentotrabajador/{id}")
    public DocumentoTrabajador buscar(@PathVariable Integer id) {
        return servicio.buscarDocumentoTrabajador(id);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @PostMapping("/api/documentotrabajador")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoTrabajador crear(@Valid @RequestBody DocumentoTrabajador entidad, BindingResult resultado) {
        validar(resultado);
        entidad.setIdDocumento(null);
        return servicio.guardarDocumentoTrabajador(entidad);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @PutMapping("/api/documentotrabajador/{id}")
    public DocumentoTrabajador actualizar(@PathVariable Integer id,
            @Valid @RequestBody DocumentoTrabajador entidad, BindingResult resultado) {
        validar(resultado);
        entidad.setIdDocumento(id);
        return servicio.guardarDocumentoTrabajador(entidad);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @DeleteMapping("/api/documentotrabajador/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) { servicio.eliminarDocumentoTrabajador(id); }

    private void validar(BindingResult resultado) {
        if (resultado.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Los datos enviados no cumplen las validaciones");
        }
    }


    @Autowired
    private ITrabajadorServicio opcionesTrabajador;

    // Limita los campos que el formulario puede asignar a la entidad y sus relaciones.
    @InitBinder("registro")
    public void configurarCampos(WebDataBinder binder) {
        binder.setAllowedFields("idDocumento", "trabajador.idTrabajador", "tipoDocumento", "fechaCarga");
    }

    // Proporciona al modelo las opciones de los selectores de relaciones, cuando existen.
    private void cargarOpciones(Model modelo) {
        modelo.addAttribute("opcionesTrabajador", opcionesTrabajador.listarTrabajador());
    }

    @GetMapping({"/documento-trabajador", "/documento-trabajador/consultar"})
    public String consultar(Model modelo) {
        // Thymeleaf obtiene esta colección para construir la tabla de consulta.
        modelo.addAttribute("registros", servicio.listarDocumentoTrabajador());
        return "documento-trabajador-consultar";
    }

    @GetMapping("/documento-trabajador/registrar")
    public String registrar(Model modelo) {
        DocumentoTrabajador registro = new DocumentoTrabajador();
        registro.setTrabajador(new Trabajador());
        modelo.addAttribute("registro", registro);
        cargarOpciones(modelo);
        return "documento-trabajador-registrar";
    }

    @GetMapping({"/documento-trabajador/consultar-id", "/documento-trabajador/detalle"})
    public String buscar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id == null) { return "documento-trabajador-consultar-id"; }
        if (!cargarRegistro(id, modelo)) { return "documento-trabajador-consultar-id"; }
        return "documento-trabajador-detalle";
    }

    @GetMapping("/documento-trabajador/actualizar")
    public String actualizar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id != null && cargarRegistro(id, modelo)) { cargarOpciones(modelo); }
        return "documento-trabajador-actualizar";
    }

    @GetMapping("/documento-trabajador/eliminar")
    public String eliminar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id != null) { cargarRegistro(id, modelo); }
        return "documento-trabajador-eliminar";
    }

    // Carga el registro o prepara un mensaje visible si el ID no existe.
    private boolean cargarRegistro(Integer id, Model modelo) {
        try {
            modelo.addAttribute("registro", servicio.buscarDocumentoTrabajador(id));
            return true;
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
            modelo.addAttribute("error", "No existe un registro con ese ID.");
            return false;
        }
    }

    @PostMapping({"/documento-trabajador/registrar", "/documento-trabajador/actualizar"})
    public String guardar(@Valid @ModelAttribute("registro") DocumentoTrabajador registro,
            BindingResult resultado, @RequestParam(required = false) MultipartFile archivo,
            jakarta.servlet.http.HttpServletRequest solicitud, Model modelo, RedirectAttributes mensajes) {
        // El mismo método recibe los formularios de registro y de actualización.
        boolean actualizando = solicitud.getRequestURI().endsWith("/actualizar");
        if (!actualizando) { registro.setIdDocumento(null); }
        if (actualizando && registro.getIdDocumento() == null) {
            resultado.reject("id", "El ID es obligatorio para actualizar.");
        }
        if (archivo != null && !archivo.isEmpty()) {
            try { registro.setArchivoBlob(archivo.getBytes()); }
            catch (IOException ex) { resultado.reject("archivo", "No se pudo leer el archivo."); }
        } else if (actualizando && registro.getIdDocumento() != null) {
            registro.setArchivoBlob(servicio.buscarDocumentoTrabajador(registro.getIdDocumento()).getArchivoBlob());
        }
        // Solo guarda si la validación de la entidad y la conversión de campos fueron correctas.
        if (!resultado.hasErrors()) {
            try {
                servicio.guardarDocumentoTrabajador(registro);
                // El mensaje flash se muestra una sola vez después de la redirección.
                mensajes.addFlashAttribute("mensaje", "Registro guardado correctamente.");
                return "redirect:/documento-trabajador/consultar";
            // Informa los conflictos de unicidad o de relaciones que detecta la base de datos.
        } catch (DataIntegrityViolationException ex) {
                resultado.reject("integridad", "No se pudo guardar. Revisa los datos únicos y las relaciones.");
            } catch (ResponseStatusException ex) {
                if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
                resultado.reject("id", "El registro ya no existe.");
            }
        }
        cargarOpciones(modelo);
        return actualizando ? "documento-trabajador-actualizar" : "documento-trabajador-registrar";
    }

    @PostMapping("/documento-trabajador/eliminar")
    public String confirmarEliminar(@RequestParam Integer id, RedirectAttributes mensajes) {
        try {
            servicio.eliminarDocumentoTrabajador(id);
            mensajes.addFlashAttribute("mensaje", "Registro eliminado correctamente.");
        // Informa los conflictos de unicidad o de relaciones que detecta la base de datos.
        } catch (DataIntegrityViolationException ex) {
            mensajes.addFlashAttribute("error", "No se puede eliminar: hay registros relacionados.");
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
            mensajes.addFlashAttribute("error", "El registro ya no existe.");
        }
        return "redirect:/documento-trabajador/consultar";
    }

}
