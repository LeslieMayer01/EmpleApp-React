import api from './api.js';

// Las funciones devuelven los datos; los errores se propagan al componente.
const ruta = '/documentotrabajador';

export async function listarDocumentosTrabajador() {
  const respuesta = await api.get(ruta);
  return respuesta.data;
}

export async function buscarDocumentoTrabajador(id) {
  const respuesta = await api.get(`${ruta}/${encodeURIComponent(id)}`);
  return respuesta.data;
}

export async function crearDocumentoTrabajador(datos) {
  const respuesta = await api.post(ruta, datos);
  return respuesta.data;
}

export async function actualizarDocumentoTrabajador(id, datos) {
  const respuesta = await api.put(`${ruta}/${encodeURIComponent(id)}`, datos);
  return respuesta.data;
}

export async function eliminarDocumentoTrabajador(id) {
  // El backend responde 204: la eliminación no devuelve un objeto.
  await api.delete(`${ruta}/${encodeURIComponent(id)}`);
}
