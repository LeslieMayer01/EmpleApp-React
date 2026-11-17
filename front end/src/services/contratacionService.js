import api from './api.js';

// Las funciones devuelven los datos; los errores se propagan al componente.
const ruta = '/contratacion';

export async function listarContrataciones() {
  const respuesta = await api.get(ruta);
  return respuesta.data;
}

export async function buscarContratacion(id) {
  const respuesta = await api.get(`${ruta}/${encodeURIComponent(id)}`);
  return respuesta.data;
}

export async function crearContratacion(datos) {
  const respuesta = await api.post(ruta, datos);
  return respuesta.data;
}

export async function actualizarContratacion(id, datos) {
  const respuesta = await api.put(`${ruta}/${encodeURIComponent(id)}`, datos);
  return respuesta.data;
}

export async function eliminarContratacion(id) {
  // El backend responde 204: la eliminación no devuelve un objeto.
  await api.delete(`${ruta}/${encodeURIComponent(id)}`);
}
