package proy.EmpleApp.servicio;

import java.util.List;
import proy.EmpleApp.modelo.Rol;

/** Define las cuatro operaciones que el controlador puede solicitar al servicio. */
public interface IRolServicio {
    /** Devuelve todos los registros de la entidad. */
    List<Rol> listarRol();
    /** Crea un registro sin ID o actualiza uno existente con ID. */
    Rol guardarRol(Rol entidad);
    /** Busca por clave primaria; si no existe, la implementación informa un error 404. */
    Rol buscarRol(Integer id);
    /** Elimina el registro identificado por su clave primaria. */
    void eliminarRol(Integer id);
}
