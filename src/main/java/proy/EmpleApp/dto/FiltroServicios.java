package proy.EmpleApp.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

/** Filtros opcionales; la primera página es 0 y se limita el tamaño a 50 resultados. */
public record FiltroServicios(
        @Size(max = 100) String busqueda,
        @Positive Integer tipoId,
        @Positive Integer estadoId,
        @Size(max = 255) String ubicacion,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
        @Min(0) Integer pagina,
        @Min(1) @Max(50) Integer tamano) {
    @AssertTrue(message = "La fecha inicial no debe ser posterior a la final")
    public boolean isRangoValido() {
        return fechaDesde == null || fechaHasta == null || !fechaDesde.isAfter(fechaHasta);
    }
}
