package proy.EmpleApp.servicio;

import java.util.List;
import proy.EmpleApp.modelo.Servicio;

/** Define las cuatro operaciones que el controlador puede solicitar al servicio. */
public interface IServicioServicio {
    /** Devuelve todos los registros de la entidad. */
    List<Servicio> listarServicio();
    /** Crea un registro sin ID o actualiza uno existente con ID. */
    Servicio guardarServicio(Servicio entidad);
    /** Busca por clave primaria; si no existe, la implementación informa un error 404. */
    Servicio buscarServicio(Integer id);
    /** Elimina el registro identificado por su clave primaria. */
    void eliminarServicio(Integer id);
}
