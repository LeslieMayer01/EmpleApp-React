package proy.EmpleApp.servicio;

import java.util.List;
import proy.EmpleApp.modelo.Postulacion;

/** Define las cuatro operaciones que el controlador puede solicitar al servicio. */
public interface IPostulacionServicio {
    /** Devuelve todos los registros de la entidad. */
    List<Postulacion> listarPostulacion();
    /** Crea un registro sin ID o actualiza uno existente con ID. */
    Postulacion guardarPostulacion(Postulacion entidad);
    /** Busca por clave primaria; si no existe, la implementación informa un error 404. */
    Postulacion buscarPostulacion(Integer id);
    /** Elimina el registro identificado por su clave primaria. */
    void eliminarPostulacion(Integer id);
}
