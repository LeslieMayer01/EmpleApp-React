package proy.EmpleApp.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Representa la tabla Pago y sus relaciones en la base de datos. */
@Entity
@Table(name = "Pago")
public class Pago {
    // Clave primaria: MySQL asigna su valor al insertar el registro.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago", nullable = false)
    private Integer idPago;

    @NotNull(message = "La relación es obligatoria")
    // Relación con un registro existente de otra entidad.
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_contratacion", nullable = false)
    private Contratacion contratacion;

    @NotNull(message = "El valor del servicio es obligatorio")
    @Positive(message = "El valor del servicio debe ser mayor que cero")
    @Digits(integer = 8, fraction = 2)
    @Column(name = "valor_servicio", precision = 10, scale = 2)
    private BigDecimal valorServicio;

    @Digits(integer = 3, fraction = 2)
    @Column(name = "porcentaje_comision_app", precision = 5, scale = 2)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal porcentajeComisionApp;

    @Digits(integer = 8, fraction = 2)
    @Column(name = "valor_comision", precision = 10, scale = 2)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal valorComision;

    @Digits(integer = 8, fraction = 2)
    @Column(name = "valor_trabajador", precision = 10, scale = 2)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal valorTrabajador;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    public static final BigDecimal COMISION_FIJA = new BigDecimal("10.00");

    /** Los importes recibidos del navegador o API nunca determinan la comision. */
    public void calcularImportes() {
        if (valorServicio == null || valorServicio.signum() <= 0
                || valorServicio.scale() > 2 || valorServicio.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new IllegalArgumentException("El pago requiere un importe positivo de hasta ocho enteros y dos decimales");
        }
        valorServicio = valorServicio.setScale(2, java.math.RoundingMode.HALF_UP);
        porcentajeComisionApp = COMISION_FIJA;
        valorComision = valorServicio.multiply(COMISION_FIJA).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
        valorTrabajador = valorServicio.subtract(valorComision);
    }

    @PrePersist
    @PreUpdate
    protected void prepararImportes() { calcularImportes(); asignarFecha(); }

    public Pago() { }

    public Integer getIdPago() { return idPago; }
    public void setIdPago(Integer idPago) { this.idPago = idPago; }

    public Contratacion getContratacion() { return contratacion; }
    public void setContratacion(Contratacion contratacion) { this.contratacion = contratacion; }

    public BigDecimal getValorServicio() { return valorServicio; }
    public void setValorServicio(BigDecimal valorServicio) { this.valorServicio = valorServicio; }

    public BigDecimal getPorcentajeComisionApp() { return porcentajeComisionApp; }
    public void setPorcentajeComisionApp(BigDecimal porcentajeComisionApp) { this.porcentajeComisionApp = porcentajeComisionApp; }

    public BigDecimal getValorComision() { return valorComision; }
    public void setValorComision(BigDecimal valorComision) { this.valorComision = valorComision; }

    public BigDecimal getValorTrabajador() { return valorTrabajador; }
    public void setValorTrabajador(BigDecimal valorTrabajador) { this.valorTrabajador = valorTrabajador; }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }

    // Asigna la fecha de creación cuando el formulario no la proporciona.
    protected void asignarFecha() {
        if (fechaPago == null) { fechaPago = LocalDateTime.now(); }
    }
}
