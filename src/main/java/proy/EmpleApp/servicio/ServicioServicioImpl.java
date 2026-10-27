package proy.EmpleApp.servicio;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import proy.EmpleApp.modelo.Servicio;
import proy.EmpleApp.repositorio.IServicioRepositorio;

/** Implementa las operaciones de la entidad usando su repositorio de Spring Data JPA. */
@Service
public class ServicioServicioImpl implements IServicioServicio {
    // Spring inyecta el repositorio; no es necesario crearlo con new.
    @Autowired
    private IServicioRepositorio repositorio;

    @Override
    public List<Servicio> listarServicio() { return repositorio.findAll(); }

    @Override
    public Servicio guardarServicio(Servicio entidad) {
        // Si tiene ID, primero comprueba que el registro que se quiere actualizar exista.
        if (entidad.getIdServicio() != null) {
            buscarServicio(entidad.getIdServicio());
        }
        return repositorio.save(entidad);
    }

    @Override
    public Servicio buscarServicio(Integer id) {
        // Convierte la ausencia del registro en una respuesta HTTP 404.
        return repositorio.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Servicio no encontrado"));
    }

    @Override
    public void eliminarServicio(Integer id) {
        repositorio.delete(buscarServicio(id));
    }
}
