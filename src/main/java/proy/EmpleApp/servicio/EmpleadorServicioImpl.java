package proy.EmpleApp.servicio;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import proy.EmpleApp.modelo.Empleador;
import proy.EmpleApp.repositorio.IEmpleadorRepositorio;

/** Implementa las operaciones de la entidad usando su repositorio de Spring Data JPA. */
@Service
public class EmpleadorServicioImpl implements IEmpleadorServicio {
    // Spring inyecta el repositorio; no es necesario crearlo con new.
    @Autowired
    private IEmpleadorRepositorio repositorio;

    @Override
    public List<Empleador> listarEmpleador() { return repositorio.findAll(); }

    @Override
    public Empleador guardarEmpleador(Empleador entidad) {
        // Si tiene ID, primero comprueba que el registro que se quiere actualizar exista.
        if (entidad.getIdEmpleador() != null) {
            buscarEmpleador(entidad.getIdEmpleador());
        }
        return repositorio.save(entidad);
    }

    @Override
    public Empleador buscarEmpleador(Integer id) {
        // Convierte la ausencia del registro en una respuesta HTTP 404.
        return repositorio.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleador no encontrado"));
    }

    @Override
    public void eliminarEmpleador(Integer id) {
        repositorio.delete(buscarEmpleador(id));
    }
}
