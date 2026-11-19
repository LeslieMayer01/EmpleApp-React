package proy.EmpleApp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import proy.EmpleApp.modelo.Servicio;

/** Respuesta pública: no serializa el correo, teléfono, dirección personal ni rol del empleador. */
public record ServicioResumen(Integer idServicio, EmpleadorPublico empleador, TipoPublico tipoServicio,
        EstadoPublico estado, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
        String ubicacion, BigDecimal valorOfertado) {
    public record NombrePublico(String nombre) { }
    public record EmpleadorPublico(Integer idEmpleador, NombrePublico usuario) { }
    public record TipoPublico(Integer idTipoServicio, String nombreTipo) { }
    public record EstadoPublico(Integer idEstado, String nombreEstado) { }

    public static ServicioResumen de(Servicio servicio) {
        var empleador = servicio.getEmpleador();
        return new ServicioResumen(servicio.getIdServicio(),
            new EmpleadorPublico(empleador.getIdEmpleador(), new NombrePublico(empleador.getUsuario().getNombre())),
            new TipoPublico(servicio.getTipoServicio().getIdTipoServicio(), servicio.getTipoServicio().getNombreTipo()),
            new EstadoPublico(servicio.getEstado().getIdEstado(), servicio.getEstado().getNombreEstado()),
            servicio.getFecha(), servicio.getHoraInicio(), servicio.getHoraFin(),
            servicio.getUbicacion(), servicio.getValorOfertado());
    }
}
