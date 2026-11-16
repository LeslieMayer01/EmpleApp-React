package proy.EmpleApp.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Representa la tabla Usuario y sus relaciones en la base de datos. */
@Entity
@Table(name = "Usuario")
public class Usuario {
    // Clave primaria: MySQL asigna su valor al insertar el registro.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @NotBlank(message = "El campo es obligatorio")
    @Size(max = 100)
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El campo es obligatorio")
    @Size(max = 100)
    @Email(message = "El correo no es válido")
    @Column(name = "correo", nullable = false, unique = true, length = 100)
    private String correo;

    @NotBlank(message = "El campo es obligatorio")
    @Size(max = 255)
    // El hash se recibe al guardar, pero se excluye de las respuestas JSON.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "contraseña_hash", nullable = false, length = 255)
    private String contrasenaHash;

    @NotNull(message = "La relación es obligatoria")
    // Relación con un registro existente de otra entidad.
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Transient
    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getEtiqueta() { return idUsuario + " - " + (nombre); }

    public Usuario() { }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre == null ? null : nombre.strip(); }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo == null ? null : correo.strip().toLowerCase(java.util.Locale.ROOT); }

    public String getContrasenaHash() { return contrasenaHash; }
    public void setContrasenaHash(String contrasenaHash) { this.contrasenaHash = contrasenaHash; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

}
