package proy.EmpleApp.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Representa la tabla Empleador y sus relaciones en la base de datos. */
@Entity
@Table(name = "Empleador")
public class Empleador {
    // Clave primaria: MySQL asigna su valor al insertar el registro.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleador", nullable = false)
    private Integer idEmpleador;

    @NotNull(message = "La relación es obligatoria")
    // Relación con un registro existente de otra entidad.
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Size(max = 255)
    @Column(name = "direccion", length = 255)
    private String direccion;

    @Size(max = 20)
    @Column(name = "telefono", length = 20)
    private String telefono;

    public Empleador() { }

    public Integer getIdEmpleador() { return idEmpleador; }
    public void setIdEmpleador(Integer idEmpleador) { this.idEmpleador = idEmpleador; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

}
