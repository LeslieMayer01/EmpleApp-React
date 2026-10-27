package proy.EmpleApp.servicio;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import proy.EmpleApp.modelo.Usuario;
import proy.EmpleApp.repositorio.IUsuarioRepositorio;

/** Implementa las operaciones de la entidad usando su repositorio de Spring Data JPA. */
@Service
public class UsuarioServicioImpl implements IUsuarioServicio {
    // Spring inyecta el repositorio; no es necesario crearlo con new.
    @Autowired
    private IUsuarioRepositorio repositorio;

    @Override
    public List<Usuario> listarUsuario() { return repositorio.findAll(); }

    @Override
    public Usuario guardarUsuario(Usuario entidad) {
        // Si tiene ID, primero comprueba que el registro que se quiere actualizar exista.
        if (entidad.getIdUsuario() != null) {
            buscarUsuario(entidad.getIdUsuario());
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
}
