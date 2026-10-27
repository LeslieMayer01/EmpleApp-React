package proy.EmpleApp.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Representa la tabla EstadoServicio y sus relaciones en la base de datos. */
@Entity
@Table(name = "EstadoServicio")
public class EstadoServicio {
    // Clave primaria: MySQL asigna su valor al insertar el registro.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado", nullable = false)
    private Integer idEstado;

    @NotBlank(message = "El campo es obligatorio")
    @Size(max = 50)
    @Column(name = "nombre_estado", nullable = false, length = 50)
    private String nombreEstado;

    public EstadoServicio() { }

    public Integer getIdEstado() { return idEstado; }
    public void setIdEstado(Integer idEstado) { this.idEstado = idEstado; }

    public String getNombreEstado() { return nombreEstado; }
    public void setNombreEstado(String nombreEstado) { this.nombreEstado = nombreEstado; }

}
