package proy.EmpleApp.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Representa la tabla Trabajador y sus relaciones en la base de datos. */
@Entity
@Table(name = "Trabajador")
public class Trabajador {
    // Clave primaria: MySQL asigna su valor al insertar el registro.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_trabajador", nullable = false)
    private Integer idTrabajador;

    @NotNull(message = "La relación es obligatoria")
    // Relación con un registro existente de otra entidad.
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "experiencia", columnDefinition = "TEXT")
    private String experiencia;

    @Size(max = 100)
    @Column(name = "disponibilidad", length = 100)
    private String disponibilidad;

    public Trabajador() { }

    public Integer getIdTrabajador() { return idTrabajador; }
    public void setIdTrabajador(Integer idTrabajador) { this.idTrabajador = idTrabajador; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getExperiencia() { return experiencia; }
    public void setExperiencia(String experiencia) { this.experiencia = experiencia; }

    public String getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(String disponibilidad) { this.disponibilidad = disponibilidad; }

}
