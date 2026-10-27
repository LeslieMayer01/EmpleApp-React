package proy.EmpleApp.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Representa la tabla Postulacion y sus relaciones en la base de datos. */
@Entity
@Table(name = "Postulacion")
public class Postulacion {
    // Clave primaria: MySQL asigna su valor al insertar el registro.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_postulacion", nullable = false)
    private Integer idPostulacion;

    @NotNull(message = "La relación es obligatoria")
    // Relación con un registro existente de otra entidad.
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_servicio", nullable = false)
    private Servicio servicio;

    @NotNull(message = "La relación es obligatoria")
    // Relación con un registro existente de otra entidad.
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_trabajador", nullable = false)
    private Trabajador trabajador;

    @Column(name = "fecha_postulacion")
    private LocalDateTime fechaPostulacion;

    public Postulacion() { }

    public Integer getIdPostulacion() { return idPostulacion; }
    public void setIdPostulacion(Integer idPostulacion) { this.idPostulacion = idPostulacion; }

    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }

    public Trabajador getTrabajador() { return trabajador; }
    public void setTrabajador(Trabajador trabajador) { this.trabajador = trabajador; }

    public LocalDateTime getFechaPostulacion() { return fechaPostulacion; }
    public void setFechaPostulacion(LocalDateTime fechaPostulacion) { this.fechaPostulacion = fechaPostulacion; }

    // Asigna la fecha de creación cuando el formulario no la proporciona.
    @PrePersist
    protected void asignarFecha() {
        if (fechaPostulacion == null) { fechaPostulacion = LocalDateTime.now(); }
    }
}
