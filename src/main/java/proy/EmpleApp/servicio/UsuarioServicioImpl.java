package proy.EmpleApp.servicio;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import proy.EmpleApp.modelo.Usuario;
import proy.EmpleApp.repositorio.IUsuarioRepositorio;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** Implementa las operaciones de la entidad usando su repositorio de Spring Data JPA. */
@Service
public class UsuarioServicioImpl implements IUsuarioServicio {
    private final BCryptPasswordEncoder contrasenas = new BCryptPasswordEncoder();
    // Spring inyecta el repositorio; no es necesario crearlo con new.
    @Autowired
    private IUsuarioRepositorio repositorio;

    @Autowired
    private jakarta.validation.Validator validador;

    @Autowired
    private proy.EmpleApp.repositorio.ITrabajadorRepositorio trabajadores;

    @Autowired
    private proy.EmpleApp.repositorio.IEmpleadorRepositorio empleadores;

    @Override
    public List<Usuario> listarUsuario() { return repositorio.findAll(); }

    @Override
    public Usuario guardarUsuario(Usuario entidad) {
        // Si tiene ID, primero comprueba que el registro que se quiere actualizar exista.
        if (entidad.getIdUsuario() != null) {
            buscarUsuario(entidad.getIdUsuario());
        }
        String clave = entidad.getContrasenaHash();
        // Conserva hashes BCrypt existentes y cifra las nuevas contraseñas antes de guardar.
        if (clave != null && !esHashBCrypt(clave)) {
            entidad.setContrasenaHash(contrasenas.encode(clave));
        }
        return repositorio.save(entidad);
    }

    @Override
    public Usuario buscarUsuario(Integer id) {
        // Convierte la ausencia del registro en una respuesta HTTP 404.
        return repositorio.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    @Override
    public void eliminarUsuario(Integer id) {
        repositorio.delete(buscarUsuario(id));
    }

    @Override
    public Usuario autenticar(String correo, String contrasena) {
        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) {
            return null;
        }
        Usuario usuario = repositorio.findByCorreo(correo.strip().toLowerCase(java.util.Locale.ROOT))
            .orElse(null);
        if (usuario == null || !esHashBCrypt(usuario.getContrasenaHash())) {
            return null;
        }
        return contrasenas.matches(contrasena, usuario.getContrasenaHash()) ? usuario : null;
    }

    @Override
    // Si falla la creación del perfil, también se revierte la creación del usuario.
    @org.springframework.transaction.annotation.Transactional
    public Usuario registrar(Usuario usuario) {
        // Registrar solo crea cuentas: un ID recibido no debe actualizar otro usuario.
        if (usuario == null || usuario.getIdUsuario() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "El registro debe corresponder a un usuario nuevo, sin ID");
        }
        if (!validador.validate(usuario).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Los datos del usuario no cumplen las validaciones");
        }

        // El modelo normaliza el correo; no se sobrescribe una cuenta existente.
        if (repositorio.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "El correo ya está registrado");
        }

        // En un registro la entrada siempre es una contraseña, nunca un hash ya calculado.
        usuario.setContrasenaHash(contrasenas.encode(usuario.getContrasenaHash()));
        Usuario registrado;
        try {
            registrado = repositorio.saveAndFlush(usuario);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            // La restricción UNIQUE también protege dos registros simultáneos con el mismo correo.
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "No se pudo registrar la cuenta. Revisa el correo y el rol", ex);
        }

        // Cuenta y perfil se guardan en una misma transacción para evitar registros incompletos.
        String nombreRol = registrado.getRol().getNombreRol().strip().toLowerCase(java.util.Locale.ROOT);
        if (nombreRol.equals("trabajador")) {
            var perfil = new proy.EmpleApp.modelo.Trabajador();
            perfil.setUsuario(registrado);
            trabajadores.save(perfil);
        } else if (nombreRol.equals("empleador")) {
            var perfil = new proy.EmpleApp.modelo.Empleador();
            perfil.setUsuario(registrado);
            empleadores.save(perfil);
        }
        return registrado;
    }

    private boolean esHashBCrypt(String valor) {
        return valor != null && valor.matches("\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}");
    }
}
