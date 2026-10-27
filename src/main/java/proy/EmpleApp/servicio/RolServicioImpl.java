package proy.EmpleApp.servicio;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import proy.EmpleApp.modelo.Rol;
import proy.EmpleApp.repositorio.IRolRepositorio;

/** Implementa las operaciones de la entidad usando su repositorio de Spring Data JPA. */
@Service
public class RolServicioImpl implements IRolServicio {
    // Spring inyecta el repositorio; no es necesario crearlo con new.
    @Autowired
    private IRolRepositorio repositorio;

    @Override
    public List<Rol> listarRol() { return repositorio.findAll(); }

    @Override
    public Rol guardarRol(Rol entidad) {
        // Si tiene ID, primero comprueba que el registro que se quiere actualizar exista.
        if (entidad.getIdRol() != null) {
            buscarRol(entidad.getIdRol());
        }
        return repositorio.save(entidad);
    }

    @Override
    public Rol buscarRol(Integer id) {
        // Convierte la ausencia del registro en una respuesta HTTP 404.
        return repositorio.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));
    }

    @Override
    public void eliminarRol(Integer id) {
        repositorio.delete(buscarRol(id));
    }
}
