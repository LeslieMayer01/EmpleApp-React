package proy.EmpleApp.servicio;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import proy.EmpleApp.modelo.Trabajador;
import proy.EmpleApp.repositorio.ITrabajadorRepositorio;

/** Implementa las operaciones de la entidad usando su repositorio de Spring Data JPA. */
@Service
public class TrabajadorServicioImpl implements ITrabajadorServicio {
    // Spring inyecta el repositorio; no es necesario crearlo con new.
    @Autowired
    private ITrabajadorRepositorio repositorio;

    @Override
    public List<Trabajador> listarTrabajador() { return repositorio.findAll(); }

    @Override
    public Trabajador guardarTrabajador(Trabajador entidad) {
        // Si tiene ID, primero comprueba que el registro que se quiere actualizar exista.
        if (entidad.getIdTrabajador() != null) {
            buscarTrabajador(entidad.getIdTrabajador());
        }
        return repositorio.save(entidad);
    }

    @Override
    public Trabajador buscarTrabajador(Integer id) {
        // Convierte la ausencia del registro en una respuesta HTTP 404.
        return repositorio.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Trabajador no encontrado"));
    }

    @Override
    public void eliminarTrabajador(Integer id) {
        repositorio.delete(buscarTrabajador(id));
    }
}
