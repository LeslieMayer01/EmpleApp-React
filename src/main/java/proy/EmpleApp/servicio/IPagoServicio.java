package proy.EmpleApp.servicio;

import java.util.List;
import proy.EmpleApp.modelo.Pago;

/** Define las cuatro operaciones que el controlador puede solicitar al servicio. */
public interface IPagoServicio {
    /** Devuelve todos los registros de la entidad. */
    List<Pago> listarPago();
    /** Crea un registro sin ID o actualiza uno existente con ID. */
    Pago guardarPago(Pago entidad);
    /** Busca por clave primaria; si no existe, la implementación informa un error 404. */
    Pago buscarPago(Integer id);
    /** Elimina el registro identificado por su clave primaria. */
    void eliminarPago(Integer id);
}
