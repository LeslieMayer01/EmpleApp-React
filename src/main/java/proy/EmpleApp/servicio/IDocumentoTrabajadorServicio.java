package proy.EmpleApp.servicio;

import java.util.List;
import proy.EmpleApp.modelo.DocumentoTrabajador;

/** Define las cuatro operaciones que el controlador puede solicitar al servicio. */
public interface IDocumentoTrabajadorServicio {
    /** Devuelve todos los registros de la entidad. */
    List<DocumentoTrabajador> listarDocumentoTrabajador();
    /** Crea un registro sin ID o actualiza uno existente con ID. */
    DocumentoTrabajador guardarDocumentoTrabajador(DocumentoTrabajador entidad);
    /** Busca por clave primaria; si no existe, la implementación informa un error 404. */
    DocumentoTrabajador buscarDocumentoTrabajador(Integer id);
    /** Elimina el registro identificado por su clave primaria. */
    void eliminarDocumentoTrabajador(Integer id);
}
