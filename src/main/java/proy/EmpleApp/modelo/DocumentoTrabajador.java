package proy.EmpleApp.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Representa la tabla DocumentoTrabajador y sus relaciones en la base de datos. */
@Entity
@Table(name = "DocumentoTrabajador")
public class DocumentoTrabajador {
    // Clave primaria: MySQL asigna su valor al insertar el registro.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento", nullable = false)
    private Integer idDocumento;

    @NotNull(message = "La relación es obligatoria")
    // Relación con un registro existente de otra entidad.
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_trabajador", nullable = false)
    private Trabajador trabajador;

    @Size(max = 100)
    @Column(name = "tipo_documento", length = 100)
    private String tipoDocumento;

    @Lob
    @Column(name = "archivo_blob", columnDefinition = "LONGBLOB")
    private byte[] archivoBlob;

    @Column(name = "fecha_carga")
    private LocalDateTime fechaCarga;

    public DocumentoTrabajador() { }

    public Integer getIdDocumento() { return idDocumento; }
    public void setIdDocumento(Integer idDocumento) { this.idDocumento = idDocumento; }

    public Trabajador getTrabajador() { return trabajador; }
    public void setTrabajador(Trabajador trabajador) { this.trabajador = trabajador; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public byte[] getArchivoBlob() { return archivoBlob; }
    public void setArchivoBlob(byte[] archivoBlob) { this.archivoBlob = archivoBlob; }

    public LocalDateTime getFechaCarga() { return fechaCarga; }
    public void setFechaCarga(LocalDateTime fechaCarga) { this.fechaCarga = fechaCarga; }

    // Asigna la fecha de creación cuando el formulario no la proporciona.
    @PrePersist
    protected void asignarFecha() {
        if (fechaCarga == null) { fechaCarga = LocalDateTime.now(); }
    }
}
