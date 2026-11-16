package proy.EmpleApp.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Representa la tabla Rol y sus relaciones en la base de datos. */
@Entity
@Table(name = "Rol")
public class Rol {
    // Clave primaria: MySQL asigna su valor al insertar el registro.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol", nullable = false)
    private Integer idRol;

    @NotBlank(message = "El campo es obligatorio")
    @Size(max = 50)
    @Column(name = "nombre_rol", nullable = false, length = 50)
    private String nombreRol;

    @Transient
    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getEtiqueta() { return idRol + " - " + (nombreRol); }

    public Rol() { }

    public Integer getIdRol() { return idRol; }
    public void setIdRol(Integer idRol) { this.idRol = idRol; }

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }

}
