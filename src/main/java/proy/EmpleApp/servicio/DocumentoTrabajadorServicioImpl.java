package proy.EmpleApp.servicio;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import proy.EmpleApp.modelo.DocumentoTrabajador;
import proy.EmpleApp.repositorio.IDocumentoTrabajadorRepositorio;

/** Implementa las operaciones de la entidad usando su repositorio de Spring Data JPA. */
@Service
public class DocumentoTrabajadorServicioImpl implements IDocumentoTrabajadorServicio {
    // Spring inyecta el repositorio; no es necesario crearlo con new.
    @Autowired
    private IDocumentoTrabajadorRepositorio repositorio;

    @Override
    public List<DocumentoTrabajador> listarDocumentoTrabajador() { return repositorio.findAll(); }

    @Override
    public DocumentoTrabajador guardarDocumentoTrabajador(DocumentoTrabajador entidad) {
        // Si tiene ID, primero comprueba que el registro que se quiere actualizar exista.
        if (entidad.getIdDocumento() != null) {
            buscarDocumentoTrabajador(entidad.getIdDocumento());
        }
        return repositorio.save(entidad);
    }

    @Override
    public DocumentoTrabajador buscarDocumentoTrabajador(Integer id) {
        // Convierte la ausencia del registro en una respuesta HTTP 404.
        return repositorio.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "DocumentoTrabajador no encontrado"));
    }

    @Override
    public void eliminarDocumentoTrabajador(Integer id) {
        repositorio.delete(buscarDocumentoTrabajador(id));
    }
}
