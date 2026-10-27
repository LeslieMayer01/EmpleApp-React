package proy.EmpleApp.servicio;

import java.util.List;
import proy.EmpleApp.modelo.Contratacion;

/** Define las cuatro operaciones que el controlador puede solicitar al servicio. */
public interface IContratacionServicio {
    /** Devuelve todos los registros de la entidad. */
    List<Contratacion> listarContratacion();
    /** Crea un registro sin ID o actualiza uno existente con ID. */
    Contratacion guardarContratacion(Contratacion entidad);
    /** Busca por clave primaria; si no existe, la implementación informa un error 404. */
    Contratacion buscarContratacion(Integer id);
    /** Elimina el registro identificado por su clave primaria. */
    void eliminarContratacion(Integer id);
}
