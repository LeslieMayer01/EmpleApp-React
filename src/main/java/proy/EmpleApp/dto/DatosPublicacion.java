package proy.EmpleApp.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/** Campos editables de una oferta: el propietario y el estado los determina el servidor. */
public record DatosPublicacion(
        @NotNull @Positive Integer idTipoServicio,
        @NotNull @FutureOrPresent LocalDate fecha,
        @NotNull LocalTime horaInicio,
        @NotNull LocalTime horaFin,
        @NotBlank @Size(max = 255) String ubicacion,
        @NotNull @Positive @Digits(integer = 8, fraction = 2) BigDecimal valorOfertado) {
    @AssertTrue(message = "La hora final debe ser posterior a la inicial del mismo día")
    @com.fasterxml.jackson.annotation.JsonIgnore
    public boolean isHorarioValido() {
        return horaInicio == null || horaFin == null || horaFin.isAfter(horaInicio);
    }
}
