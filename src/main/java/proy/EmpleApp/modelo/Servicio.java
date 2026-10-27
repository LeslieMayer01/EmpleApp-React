package proy.EmpleApp.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Representa la tabla Servicio y sus relaciones en la base de datos. */
@Entity
@Table(name = "Servicio")
public class Servicio {
    // Clave primaria: MySQL asigna su valor al insertar el registro.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio", nullable = false)
    private Integer idServicio;

    @NotNull(message = "La relación es obligatoria")
    // Relación con un registro existente de otra entidad.
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_empleador", nullable = false)
    private Empleador empleador;

    @NotNull(message = "La relación es obligatoria")
    // Relación con un registro existente de otra entidad.
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_tipo_servicio", nullable = false)
    private TipoServicio tipoServicio;

    @NotNull(message = "La relación es obligatoria")
    // Relación con un registro existente de otra entidad.
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_estado", nullable = false)
    private EstadoServicio estado;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;

    @Size(max = 255)
    @Column(name = "ubicacion", length = 255)
    private String ubicacion;

    @Digits(integer = 8, fraction = 2)
    @Column(name = "valor_ofertado", precision = 10, scale = 2)
    private BigDecimal valorOfertado;

    public Servicio() { }

    public Integer getIdServicio() { return idServicio; }
    public void setIdServicio(Integer idServicio) { this.idServicio = idServicio; }

    public Empleador getEmpleador() { return empleador; }
    public void setEmpleador(Empleador empleador) { this.empleador = empleador; }

    public TipoServicio getTipoServicio() { return tipoServicio; }
    public void setTipoServicio(TipoServicio tipoServicio) { this.tipoServicio = tipoServicio; }

    public EstadoServicio getEstado() { return estado; }
    public void setEstado(EstadoServicio estado) { this.estado = estado; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public BigDecimal getValorOfertado() { return valorOfertado; }
    public void setValorOfertado(BigDecimal valorOfertado) { this.valorOfertado = valorOfertado; }

}
