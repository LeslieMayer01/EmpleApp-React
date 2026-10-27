package proy.EmpleApp.servicio;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import proy.EmpleApp.modelo.Pago;
import proy.EmpleApp.repositorio.IPagoRepositorio;

/** Implementa las operaciones de la entidad usando su repositorio de Spring Data JPA. */
@Service
public class PagoServicioImpl implements IPagoServicio {
    // Spring inyecta el repositorio; no es necesario crearlo con new.
    @Autowired
    private IPagoRepositorio repositorio;

    @Override
    public List<Pago> listarPago() { return repositorio.findAll(); }

    @Override
    public Pago guardarPago(Pago entidad) {
        // Si tiene ID, primero comprueba que el registro que se quiere actualizar exista.
        if (entidad.getIdPago() != null) {
            buscarPago(entidad.getIdPago());
        }
        return repositorio.save(entidad);
    }

    @Override
    public Pago buscarPago(Integer id) {
        // Convierte la ausencia del registro en una respuesta HTTP 404.
        return repositorio.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago no encontrado"));
    }

    @Override
    public void eliminarPago(Integer id) {
        repositorio.delete(buscarPago(id));
    }
}
