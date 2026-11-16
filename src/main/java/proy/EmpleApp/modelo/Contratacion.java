package proy.EmpleApp.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Representa la tabla Contratacion y sus relaciones en la base de datos. */
@Entity
@Table(name = "Contratacion")
public class Contratacion {
    // Clave primaria: MySQL asigna su valor al insertar el registro.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contratacion", nullable = false)
    private Integer idContratacion;

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

    @Column(name = "fecha_contratacion")
    private LocalDateTime fechaContratacion;

    @Transient
    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getEtiqueta() { return idContratacion + " - " + ((trabajador == null ? "Sin trabajador" : trabajador.getEtiqueta()) + " / " + (servicio == null ? "Sin servicio" : servicio.getEtiqueta())); }

    public Contratacion() { }

    public Integer getIdContratacion() { return idContratacion; }
    public void setIdContratacion(Integer idContratacion) { this.idContratacion = idContratacion; }

    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }

    public Trabajador getTrabajador() { return trabajador; }
    public void setTrabajador(Trabajador trabajador) { this.trabajador = trabajador; }

    public LocalDateTime getFechaContratacion() { return fechaContratacion; }
    public void setFechaContratacion(LocalDateTime fechaContratacion) { this.fechaContratacion = fechaContratacion; }

    // Asigna la fecha de creación cuando el formulario no la proporciona.
    @PrePersist
    protected void asignarFecha() {
        if (fechaContratacion == null) { fechaContratacion = LocalDateTime.now(); }
    }
}
