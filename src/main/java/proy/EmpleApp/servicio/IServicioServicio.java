package proy.EmpleApp.servicio;

import java.util.List;
import proy.EmpleApp.modelo.Servicio;

/** Define el CRUD administrativo y las operaciones propias de publicación del empleador. */
public interface IServicioServicio {
    /** Devuelve todos los registros de la entidad. */
    List<Servicio> listarServicio();
    /** Crea un registro sin ID o actualiza uno existente con ID. */
    Servicio guardarServicio(Servicio entidad);
    /** Busca por clave primaria; si no existe, la implementación informa un error 404. */
    Servicio buscarServicio(Integer id);
    /** Elimina el registro identificado por su clave primaria. */
    void eliminarServicio(Integer id);

    proy.EmpleApp.dto.PaginaServicios buscarPublicaciones(proy.EmpleApp.dto.FiltroServicios filtro,
        org.springframework.security.core.Authentication autenticacion, boolean soloMias);

    proy.EmpleApp.dto.ServicioResumen publicar(proy.EmpleApp.dto.DatosPublicacion datos,
        org.springframework.security.core.Authentication autenticacion);

    proy.EmpleApp.dto.ServicioResumen actualizarPublicacion(Integer id, proy.EmpleApp.dto.DatosPublicacion datos,
        org.springframework.security.core.Authentication autenticacion);

    proy.EmpleApp.dto.ServicioResumen cancelarPublicacion(Integer id,
        org.springframework.security.core.Authentication autenticacion);
}
