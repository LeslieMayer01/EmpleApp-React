package proy.EmpleApp.servicio;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import proy.EmpleApp.modelo.EstadoServicio;
import proy.EmpleApp.repositorio.IEstadoServicioRepositorio;

/** Implementa las operaciones de la entidad usando su repositorio de Spring Data JPA. */
@Service
public class EstadoServicioServicioImpl implements IEstadoServicioServicio {
    // Spring inyecta el repositorio; no es necesario crearlo con new.
    @Autowired
    private IEstadoServicioRepositorio repositorio;

    @Override
    public List<EstadoServicio> listarEstadoServicio() { return repositorio.findAll(); }

    @Override
    public EstadoServicio guardarEstadoServicio(EstadoServicio entidad) {
        // Si tiene ID, primero comprueba que el registro que se quiere actualizar exista.
        if (entidad.getIdEstado() != null) {
            buscarEstadoServicio(entidad.getIdEstado());
        }
        return repositorio.save(entidad);
    }

    @Override
    public EstadoServicio buscarEstadoServicio(Integer id) {
        // Convierte la ausencia del registro en una respuesta HTTP 404.
        return repositorio.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "EstadoServicio no encontrado"));
    }

    @Override
    public void eliminarEstadoServicio(Integer id) {
        repositorio.delete(buscarEstadoServicio(id));
    }
}
