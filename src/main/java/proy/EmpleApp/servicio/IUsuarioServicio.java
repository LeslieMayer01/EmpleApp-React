package proy.EmpleApp.servicio;

import java.util.List;
import proy.EmpleApp.modelo.Usuario;

/** Define las cuatro operaciones que el controlador puede solicitar al servicio. */
public interface IUsuarioServicio {
    /** Devuelve todos los registros de la entidad. */
    List<Usuario> listarUsuario();
    /** Crea un registro sin ID o actualiza uno existente con ID. */
    Usuario guardarUsuario(Usuario entidad);
    /** Busca por clave primaria; si no existe, la implementación informa un error 404. */
    Usuario buscarUsuario(Integer id);
    /** Elimina el registro identificado por su clave primaria. */
    void eliminarUsuario(Integer id);
}
