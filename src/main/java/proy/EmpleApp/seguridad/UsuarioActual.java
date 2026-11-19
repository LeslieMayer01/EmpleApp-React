package proy.EmpleApp.seguridad;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import proy.EmpleApp.modelo.Usuario;
import proy.EmpleApp.repositorio.IUsuarioRepositorio;

/** Obtiene la identidad del servidor, nunca de un ID enviado por el navegador. */
@Component
public class UsuarioActual {
    private final IUsuarioRepositorio usuarios;

    public UsuarioActual(IUsuarioRepositorio usuarios) {
        this.usuarios = usuarios;
    }

    public Usuario obtener(Authentication autenticacion) {
        if (autenticacion == null || !autenticacion.isAuthenticated()
                || autenticacion instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión");
        }
        try {
            Integer id = Integer.valueOf(autenticacion.getName());
            return usuarios.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La cuenta ya no existe"));
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión inválida");
        }
    }

    public boolean esAdministrador(Authentication autenticacion) {
        try {
            // El rol se consulta de nuevo para que un cambio de permisos tenga efecto inmediato.
            String rol = obtener(autenticacion).getRol().getNombreRol()
                .strip().toLowerCase(java.util.Locale.ROOT);
            return rol.equals("administrador") || rol.startsWith("administrador ");
        } catch (ResponseStatusException ex) {
            return false;
        }
    }

    public Usuario exigirRol(Authentication autenticacion, String esperado) {
        Usuario usuario = obtener(autenticacion);
        if (!usuario.getRol().getNombreRol().strip().equalsIgnoreCase(esperado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Tu cuenta no tiene un perfil de " + esperado);
        }
        return usuario;
    }
}
