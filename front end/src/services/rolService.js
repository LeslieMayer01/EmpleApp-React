import api from './api.js';

// Las funciones devuelven los datos; los errores se propagan al componente.
const ruta = '/rol';

export async function listarRoles() {
  const respuesta = await api.get(ruta);
  return respuesta.data;
}

export async function buscarRol(id) {
  const respuesta = await api.get(`${ruta}/${encodeURIComponent(id)}`);
  return respuesta.data;
}

export async function crearRol(datos) {
  const respuesta = await api.post(ruta, datos);
  return respuesta.data;
}

export async function actualizarRol(id, datos) {
  const respuesta = await api.put(`${ruta}/${encodeURIComponent(id)}`, datos);
  return respuesta.data;
}

export async function eliminarRol(id) {
  // El backend responde 204: la eliminación no devuelve un objeto.
  await api.delete(`${ruta}/${encodeURIComponent(id)}`);
}
