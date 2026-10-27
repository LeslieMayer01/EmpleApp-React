package proy.EmpleApp.servicio;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import proy.EmpleApp.modelo.TipoServicio;
import proy.EmpleApp.repositorio.ITipoServicioRepositorio;

/** Implementa las operaciones de la entidad usando su repositorio de Spring Data JPA. */
@Service
public class TipoServicioServicioImpl implements ITipoServicioServicio {
    // Spring inyecta el repositorio; no es necesario crearlo con new.
    @Autowired
    private ITipoServicioRepositorio repositorio;

    @Override
    public List<TipoServicio> listarTipoServicio() { return repositorio.findAll(); }

    @Override
    public TipoServicio guardarTipoServicio(TipoServicio entidad) {
        // Si tiene ID, primero comprueba que el registro que se quiere actualizar exista.
        if (entidad.getIdTipoServicio() != null) {
            buscarTipoServicio(entidad.getIdTipoServicio());
        }
        return repositorio.save(entidad);
    }

    @Override
    public TipoServicio buscarTipoServicio(Integer id) {
        // Convierte la ausencia del registro en una respuesta HTTP 404.
        return repositorio.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "TipoServicio no encontrado"));
    }

    @Override
    public void eliminarTipoServicio(Integer id) {
        repositorio.delete(buscarTipoServicio(id));
    }
}
