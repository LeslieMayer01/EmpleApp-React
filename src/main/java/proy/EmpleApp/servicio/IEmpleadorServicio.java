package proy.EmpleApp.servicio;

import java.util.List;
import proy.EmpleApp.modelo.Empleador;

/** Define las cuatro operaciones que el controlador puede solicitar al servicio. */
public interface IEmpleadorServicio {
    /** Devuelve todos los registros de la entidad. */
    List<Empleador> listarEmpleador();
    /** Crea un registro sin ID o actualiza uno existente con ID. */
    Empleador guardarEmpleador(Empleador entidad);
    /** Busca por clave primaria; si no existe, la implementación informa un error 404. */
    Empleador buscarEmpleador(Integer id);
    /** Elimina el registro identificado por su clave primaria. */
    void eliminarEmpleador(Integer id);
}
