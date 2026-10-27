package proy.EmpleApp.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Representa la tabla TipoServicio y sus relaciones en la base de datos. */
@Entity
@Table(name = "TipoServicio")
public class TipoServicio {
    // Clave primaria: MySQL asigna su valor al insertar el registro.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_servicio", nullable = false)
    private Integer idTipoServicio;

    @NotBlank(message = "El campo es obligatorio")
    @Size(max = 100)
    @Column(name = "nombre_tipo", nullable = false, length = 100)
    private String nombreTipo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    public TipoServicio() { }

    public Integer getIdTipoServicio() { return idTipoServicio; }
    public void setIdTipoServicio(Integer idTipoServicio) { this.idTipoServicio = idTipoServicio; }

    public String getNombreTipo() { return nombreTipo; }
    public void setNombreTipo(String nombreTipo) { this.nombreTipo = nombreTipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

}
