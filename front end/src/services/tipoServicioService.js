import api from './api.js';

// Las funciones devuelven los datos; los errores se propagan al componente.
const ruta = '/tiposervicio';

export async function listarTiposServicio() {
  const respuesta = await api.get(ruta);
  return respuesta.data;
}

export async function buscarTipoServicio(id) {
  const respuesta = await api.get(`${ruta}/${encodeURIComponent(id)}`);
  return respuesta.data;
}

export async function crearTipoServicio(datos) {
  const respuesta = await api.post(ruta, datos);
  return respuesta.data;
}

export async function actualizarTipoServicio(id, datos) {
  const respuesta = await api.put(`${ruta}/${encodeURIComponent(id)}`, datos);
  return respuesta.data;
}

export async function eliminarTipoServicio(id) {
  // El backend responde 204: la eliminación no devuelve un objeto.
  await api.delete(`${ruta}/${encodeURIComponent(id)}`);
}
