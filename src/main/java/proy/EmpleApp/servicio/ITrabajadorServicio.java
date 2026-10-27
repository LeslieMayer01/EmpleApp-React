package proy.EmpleApp.servicio;

import java.util.List;
import proy.EmpleApp.modelo.Trabajador;

/** Define las cuatro operaciones que el controlador puede solicitar al servicio. */
public interface ITrabajadorServicio {
    /** Devuelve todos los registros de la entidad. */
    List<Trabajador> listarTrabajador();
    /** Crea un registro sin ID o actualiza uno existente con ID. */
    Trabajador guardarTrabajador(Trabajador entidad);
    /** Busca por clave primaria; si no existe, la implementación informa un error 404. */
    Trabajador buscarTrabajador(Integer id);
    /** Elimina el registro identificado por su clave primaria. */
    void eliminarTrabajador(Integer id);
}
