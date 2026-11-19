import api from './api.js';

// Las funciones devuelven los datos; los errores se propagan al componente.
const ruta = '/trabajador';

export async function listarTrabajadores() {
  const respuesta = await api.get(ruta);
  return respuesta.data;
}

export async function buscarTrabajador(id) {
  const respuesta = await api.get(`${ruta}/${encodeURIComponent(id)}`);
  return respuesta.data;
}

export async function crearTrabajador(datos) {
  const respuesta = await api.post(ruta, datos);
  return respuesta.data;
}

export async function actualizarTrabajador(id, datos) {
  const respuesta = await api.put(`${ruta}/${encodeURIComponent(id)}`, datos);
  return respuesta.data;
}

export async function eliminarTrabajador(id) {
  // El backend responde 204: la eliminación no devuelve un objeto.
  await api.delete(`${ruta}/${encodeURIComponent(id)}`);
}

// La sesión identifica al propietario; no se envía un ID de usuario.
export async function obtenerMiPerfilTrabajador() {
  const respuesta = await api.get('/trabajador/me');
  return respuesta.data;
}

export async function actualizarMiPerfilTrabajador(datos) {
  const respuesta = await api.put('/trabajador/me', datos);
  return respuesta.data;
}
