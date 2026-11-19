import api from './api.js';

// Las funciones devuelven los datos; los errores se propagan al componente.
const ruta = '/empleador';

export async function listarEmpleadores() {
  const respuesta = await api.get(ruta);
  return respuesta.data;
}

export async function buscarEmpleador(id) {
  const respuesta = await api.get(`${ruta}/${encodeURIComponent(id)}`);
  return respuesta.data;
}

export async function crearEmpleador(datos) {
  const respuesta = await api.post(ruta, datos);
  return respuesta.data;
}

export async function actualizarEmpleador(id, datos) {
  const respuesta = await api.put(`${ruta}/${encodeURIComponent(id)}`, datos);
  return respuesta.data;
}

export async function eliminarEmpleador(id) {
  // El backend responde 204: la eliminación no devuelve un objeto.
  await api.delete(`${ruta}/${encodeURIComponent(id)}`);
}

// La sesión identifica al propietario; no se envía un ID de usuario.
export async function obtenerMiPerfilEmpleador() {
  const respuesta = await api.get('/empleador/me');
  return respuesta.data;
}

export async function actualizarMiPerfilEmpleador(datos) {
  const respuesta = await api.put('/empleador/me', datos);
  return respuesta.data;
}
