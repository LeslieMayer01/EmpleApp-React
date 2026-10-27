package proy.EmpleApp.servicio;

import java.util.List;
import proy.EmpleApp.modelo.EstadoServicio;

/** Define las cuatro operaciones que el controlador puede solicitar al servicio. */
public interface IEstadoServicioServicio {
    /** Devuelve todos los registros de la entidad. */
    List<EstadoServicio> listarEstadoServicio();
    /** Crea un registro sin ID o actualiza uno existente con ID. */
    EstadoServicio guardarEstadoServicio(EstadoServicio entidad);
    /** Busca por clave primaria; si no existe, la implementación informa un error 404. */
    EstadoServicio buscarEstadoServicio(Integer id);
    /** Elimina el registro identificado por su clave primaria. */
    void eliminarEstadoServicio(Integer id);
}
