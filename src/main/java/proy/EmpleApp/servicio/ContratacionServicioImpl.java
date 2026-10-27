package proy.EmpleApp.servicio;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import proy.EmpleApp.modelo.Contratacion;
import proy.EmpleApp.repositorio.IContratacionRepositorio;

/** Implementa las operaciones de la entidad usando su repositorio de Spring Data JPA. */
@Service
public class ContratacionServicioImpl implements IContratacionServicio {
    // Spring inyecta el repositorio; no es necesario crearlo con new.
    @Autowired
    private IContratacionRepositorio repositorio;

    @Override
    public List<Contratacion> listarContratacion() { return repositorio.findAll(); }

    @Override
    public Contratacion guardarContratacion(Contratacion entidad) {
        // Si tiene ID, primero comprueba que el registro que se quiere actualizar exista.
        if (entidad.getIdContratacion() != null) {
            buscarContratacion(entidad.getIdContratacion());
        }
        return repositorio.save(entidad);
    }

    @Override
    public Contratacion buscarContratacion(Integer id) {
        // Convierte la ausencia del registro en una respuesta HTTP 404.
        return repositorio.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Contratacion no encontrado"));
    }

    @Override
    public void eliminarContratacion(Integer id) {
        repositorio.delete(buscarContratacion(id));
    }
}
