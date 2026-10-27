package proy.EmpleApp.servicio;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import proy.EmpleApp.modelo.Postulacion;
import proy.EmpleApp.repositorio.IPostulacionRepositorio;

/** Implementa las operaciones de la entidad usando su repositorio de Spring Data JPA. */
@Service
public class PostulacionServicioImpl implements IPostulacionServicio {
    // Spring inyecta el repositorio; no es necesario crearlo con new.
    @Autowired
    private IPostulacionRepositorio repositorio;

    @Override
    public List<Postulacion> listarPostulacion() { return repositorio.findAll(); }

    @Override
    public Postulacion guardarPostulacion(Postulacion entidad) {
        // Si tiene ID, primero comprueba que el registro que se quiere actualizar exista.
        if (entidad.getIdPostulacion() != null) {
            buscarPostulacion(entidad.getIdPostulacion());
        }
        return repositorio.save(entidad);
    }

    @Override
    public Postulacion buscarPostulacion(Integer id) {
        // Convierte la ausencia del registro en una respuesta HTTP 404.
        return repositorio.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Postulacion no encontrado"));
    }

    @Override
    public void eliminarPostulacion(Integer id) {
        repositorio.delete(buscarPostulacion(id));
    }
}
