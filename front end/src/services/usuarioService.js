import api from './api.js';

// Las funciones devuelven los datos; los errores se propagan al componente.
const ruta = '/usuario';

export async function listarUsuarios() {
  const respuesta = await api.get(ruta);
  return respuesta.data;
}

export async function buscarUsuario(id) {
  const respuesta = await api.get(`${ruta}/${encodeURIComponent(id)}`);
  return respuesta.data;
}

export async function crearUsuario(datos) {
  const respuesta = await api.post(ruta, datos);
  return respuesta.data;
}

export async function actualizarUsuario(id, datos) {
  const respuesta = await api.put(`${ruta}/${encodeURIComponent(id)}`, datos);
  return respuesta.data;
}

export async function eliminarUsuario(id) {
  // El backend responde 204: la eliminación no devuelve un objeto.
  await api.delete(`${ruta}/${encodeURIComponent(id)}`);
}

// El backend recibe correo y contrasena, y devuelve el usuario sin su contraseña.
export async function iniciarSesion(correo, contrasena) {
  const respuesta = await api.post(`${ruta}/login`, { correo, contrasena });
  return respuesta.data;
}

// La ruta de registro valida el rol y crea también el perfil correspondiente.
// Recibe nombre, correo, contrasena e idRol. Axios propaga los errores al formulario.
export async function registrarUsuario(datos) {
  const respuesta = await api.post(`${ruta}/registro`, datos);
  return respuesta.data;
}
