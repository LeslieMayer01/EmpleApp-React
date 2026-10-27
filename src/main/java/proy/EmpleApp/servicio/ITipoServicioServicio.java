package proy.EmpleApp.servicio;

import java.util.List;
import proy.EmpleApp.modelo.TipoServicio;

/** Define las cuatro operaciones que el controlador puede solicitar al servicio. */
public interface ITipoServicioServicio {
    /** Devuelve todos los registros de la entidad. */
    List<TipoServicio> listarTipoServicio();
    /** Crea un registro sin ID o actualiza uno existente con ID. */
    TipoServicio guardarTipoServicio(TipoServicio entidad);
    /** Busca por clave primaria; si no existe, la implementación informa un error 404. */
    TipoServicio buscarTipoServicio(Integer id);
    /** Elimina el registro identificado por su clave primaria. */
    void eliminarTipoServicio(Integer id);
}
