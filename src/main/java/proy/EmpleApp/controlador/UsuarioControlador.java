package proy.EmpleApp.controlador;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import proy.EmpleApp.modelo.Usuario;
import proy.EmpleApp.servicio.IUsuarioServicio;
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
public class UsuarioControlador {

    @Autowired
    private IUsuarioServicio servicio;

    @Autowired
    private proy.EmpleApp.seguridad.UsuarioActual usuarioActual;
    @Autowired
    private org.springframework.security.web.context.HttpSessionSecurityContextRepository contextos;
    @Autowired
    private org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository tokensCsrf;

    // React y Postman solicitan este token antes de enviar POST, PUT, PATCH o DELETE.
    @ResponseBody
    @GetMapping("/api/usuario/csrf")
    public java.util.Map<String, String> csrf(org.springframework.security.web.csrf.CsrfToken token) {
        return java.util.Map.of("token", token.getToken(), "headerName", token.getHeaderName());
    }

    @ResponseBody
    @GetMapping("/api/usuario/me")
    public Usuario cuentaActual(org.springframework.security.core.Authentication autenticacion) {
        return usuarioActual.obtener(autenticacion);
    }

    // El login solo requiere correo y contraseña, no los demás campos del registro.
    public record SolicitudLogin(
            @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Email String correo,
            @jakarta.validation.constraints.NotBlank
            @jakarta.validation.constraints.Size(max = 72) String contrasena) { }

    @ResponseBody
    @PostMapping("/api/usuario/login")
    public ResponseEntity<?> login(@Valid @RequestBody SolicitudLogin solicitud,
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response) {
        Usuario usuarioAutenticado = servicio.autenticar(solicitud.correo(), solicitud.contrasena());
        if (usuarioAutenticado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Correo o contraseña incorrectos");
        }
        // Cambia el ID al autenticar para evitar reutilizar un identificador de sesión previo.
        request.getSession();
        request.changeSessionId();
        var autenticacion = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
            usuarioAutenticado.getIdUsuario().toString(), null, java.util.List.of());
        var contexto = org.springframework.security.core.context.SecurityContextHolder.createEmptyContext();
        contexto.setAuthentication(autenticacion);
        org.springframework.security.core.context.SecurityContextHolder.setContext(contexto);
        contextos.saveContext(contexto, request, response);
        // El siguiente envío debe obtener un token CSRF nuevo después del login.
        tokensCsrf.saveToken(null, request, response);
        return ResponseEntity.ok(usuarioAutenticado);
    }

    // Solo se reciben los campos del formulario, nunca el ID ni un hash proporcionado por el cliente.
    public record SolicitudRegistro(
            @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max = 100) String nombre,
            @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Email
            @jakarta.validation.constraints.Size(max = 100) String correo,
            @jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max = 72) String contrasena,
            @jakarta.validation.constraints.NotNull Integer idRol) { }

    @ResponseBody
    @PostMapping("/api/usuario/registro")
    // Devuelve 201 cuando se crean la cuenta y su perfil correctamente.
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario registrar(@Valid @RequestBody SolicitudRegistro solicitud) {
        Rol rol = opcionesRol.buscarRol(solicitud.idRol());
        String nombreRol = rol.getNombreRol().strip().toLowerCase(java.util.Locale.ROOT);
        // El registro público no permite crear administradores, aunque se altere el formulario.
        if (!nombreRol.equals("trabajador") && !nombreRol.equals("empleador")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Selecciona el rol Trabajador o Empleador");
        }
        // El límite de BCrypt se mide en bytes UTF-8, no solo en cantidad de caracteres.
        if (solicitud.contrasena().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "La contraseña debe ocupar como máximo 72 bytes");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(solicitud.nombre());
        usuario.setCorreo(solicitud.correo());
        // El formulario envía la contraseña; el servicio genera el hash antes de persistirla.
        usuario.setContrasenaHash(solicitud.contrasena());
        usuario.setRol(rol);
        return servicio.registrar(usuario);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @GetMapping("/api/usuario")
    public List<Usuario> listar(Model modelo) {
        List<Usuario> registros = servicio.listarUsuario();
        modelo.addAttribute("registros", registros);
        return registros;
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @GetMapping("/api/usuario/{id}")
    public Usuario buscar(@PathVariable Integer id) {
        return servicio.buscarUsuario(id);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @PostMapping("/api/usuario")
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario crear(@Valid @RequestBody Usuario entidad, BindingResult resultado) {
        validar(resultado);
        entidad.setIdUsuario(null);
        return servicio.guardarUsuario(entidad);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @PutMapping("/api/usuario/{id}")
    public Usuario actualizar(@PathVariable Integer id,
            @Valid @RequestBody Usuario entidad, BindingResult resultado) {
        validar(resultado);
        entidad.setIdUsuario(id);
        return servicio.guardarUsuario(entidad);
    }

    // Esta ruta de la API devuelve datos JSON en lugar de una plantilla HTML.
    @ResponseBody
    @DeleteMapping("/api/usuario/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) { servicio.eliminarUsuario(id); }

    private void validar(BindingResult resultado) {
        if (resultado.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Los datos enviados no cumplen las validaciones");
        }
    }


    @Autowired
    private IRolServicio opcionesRol;

    // Limita los campos que el formulario puede asignar a la entidad y sus relaciones.
    @InitBinder("registro")
    public void configurarCampos(WebDataBinder binder) {
        binder.setAllowedFields("idUsuario", "nombre", "correo", "contrasenaHash", "rol.idRol");
    }

    // Proporciona al modelo las opciones de los selectores de relaciones, cuando existen.
    private void cargarOpciones(Model modelo) {
        modelo.addAttribute("opcionesRol", opcionesRol.listarRol());
    }

    @GetMapping({"/usuario", "/usuario/consultar"})
    public String consultar(Model modelo) {
        // Thymeleaf obtiene esta colección para construir la tabla de consulta.
        modelo.addAttribute("registros", servicio.listarUsuario());
        return "usuario-consultar";
    }

    @GetMapping("/usuario/registrar")
    public String registrar(Model modelo) {
        Usuario registro = new Usuario();
        registro.setRol(new Rol());
        modelo.addAttribute("registro", registro);
        cargarOpciones(modelo);
        return "usuario-registrar";
    }

    @GetMapping({"/usuario/consultar-id", "/usuario/detalle"})
    public String buscar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id == null) { return "usuario-consultar-id"; }
        if (!cargarRegistro(id, modelo)) { return "usuario-consultar-id"; }
        return "usuario-detalle";
    }

    @GetMapping("/usuario/actualizar")
    public String actualizar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id != null && cargarRegistro(id, modelo)) { cargarOpciones(modelo); }
        return "usuario-actualizar";
    }

    @GetMapping("/usuario/eliminar")
    public String eliminar(@RequestParam(required = false) Integer id, Model modelo) {
        if (id != null) { cargarRegistro(id, modelo); }
        return "usuario-eliminar";
    }

    // Carga el registro o prepara un mensaje visible si el ID no existe.
    private boolean cargarRegistro(Integer id, Model modelo) {
        try {
            modelo.addAttribute("registro", servicio.buscarUsuario(id));
            return true;
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
            modelo.addAttribute("error", "No existe un registro con ese ID.");
            return false;
        }
    }

    @PostMapping({"/usuario/registrar", "/usuario/actualizar"})
    public String guardar(@Valid @ModelAttribute("registro") Usuario registro,
            BindingResult resultado, @RequestParam(required = false) MultipartFile archivo,
            jakarta.servlet.http.HttpServletRequest solicitud, Model modelo, RedirectAttributes mensajes) {
        // El mismo método recibe los formularios de registro y de actualización.
        boolean actualizando = solicitud.getRequestURI().endsWith("/actualizar");
        if (!actualizando) { registro.setIdUsuario(null); }
        if (actualizando && registro.getIdUsuario() == null) {
            resultado.reject("id", "El ID es obligatorio para actualizar.");
        }
        // Solo guarda si la validación de la entidad y la conversión de campos fueron correctas.
        if (!resultado.hasErrors()) {
            try {
                servicio.guardarUsuario(registro);
                // El mensaje flash se muestra una sola vez después de la redirección.
                mensajes.addFlashAttribute("mensaje", "Registro guardado correctamente.");
                return "redirect:/usuario/consultar";
            // Informa los conflictos de unicidad o de relaciones que detecta la base de datos.
        } catch (DataIntegrityViolationException ex) {
                resultado.reject("integridad", "No se pudo guardar. Revisa los datos únicos y las relaciones.");
            } catch (ResponseStatusException ex) {
                if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
                resultado.reject("id", "El registro ya no existe.");
            }
        }
        cargarOpciones(modelo);
        return actualizando ? "usuario-actualizar" : "usuario-registrar";
    }

    @PostMapping("/usuario/eliminar")
    public String confirmarEliminar(@RequestParam Integer id, RedirectAttributes mensajes) {
        try {
            servicio.eliminarUsuario(id);
            mensajes.addFlashAttribute("mensaje", "Registro eliminado correctamente.");
        // Informa los conflictos de unicidad o de relaciones que detecta la base de datos.
        } catch (DataIntegrityViolationException ex) {
            mensajes.addFlashAttribute("error", "No se puede eliminar: hay registros relacionados.");
        } catch (ResponseStatusException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) { throw ex; }
            mensajes.addFlashAttribute("error", "El registro ya no existe.");
        }
        return "redirect:/usuario/consultar";
    }

}
