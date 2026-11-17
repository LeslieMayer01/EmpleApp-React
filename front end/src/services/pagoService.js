import api from './api.js';

// Las funciones devuelven los datos; los errores se propagan al componente.
const ruta = '/pago';

export async function listarPagos() {
  const respuesta = await api.get(ruta);
  return respuesta.data;
}

export async function buscarPago(id) {
  const respuesta = await api.get(`${ruta}/${encodeURIComponent(id)}`);
  return respuesta.data;
}

export async function crearPago(datos) {
  const respuesta = await api.post(ruta, datos);
  return respuesta.data;
}

export async function actualizarPago(id, datos) {
  const respuesta = await api.put(`${ruta}/${encodeURIComponent(id)}`, datos);
  return respuesta.data;
}

export async function eliminarPago(id) {
  // El backend responde 204: la eliminación no devuelve un objeto.
  await api.delete(`${ruta}/${encodeURIComponent(id)}`);
}
