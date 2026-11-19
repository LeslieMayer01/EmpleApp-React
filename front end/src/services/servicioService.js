import api from './api.js';

// Las funciones devuelven los datos; los errores se propagan al componente.
const ruta = '/servicio';

export async function listarServicios() {
  const respuesta = await api.get(ruta);
  return respuesta.data;
}

export async function buscarServicio(id) {
  const respuesta = await api.get(`${ruta}/${encodeURIComponent(id)}`);
  return respuesta.data;
}

export async function crearServicio(datos) {
  const respuesta = await api.post(ruta, datos);
  return respuesta.data;
}

export async function actualizarServicio(id, datos) {
  const respuesta = await api.put(`${ruta}/${encodeURIComponent(id)}`, datos);
  return respuesta.data;
}

export async function eliminarServicio(id) {
  // El backend responde 204: la eliminación no devuelve un objeto.
  await api.delete(`${ruta}/${encodeURIComponent(id)}`);
}

// Catálogo paginado y operaciones propias: el backend deriva el empleador de la sesión.
export async function buscarPublicaciones(filtros = {}, soloMias = false) {
  const rutaConsulta = soloMias ? `${ruta}/mis-publicaciones` : `${ruta}/buscar`;
  const respuesta = await api.get(rutaConsulta, { params: filtros });
  return respuesta.data;
}

export async function publicarServicio(datos) {
  const respuesta = await api.post(`${ruta}/mis-publicaciones`, datos);
  return respuesta.data;
}

export async function actualizarPublicacion(id, datos) {
  const respuesta = await api.put(`${ruta}/mis-publicaciones/${encodeURIComponent(id)}`, datos);
  return respuesta.data;
}

export async function cancelarPublicacion(id) {
  const respuesta = await api.patch(`${ruta}/mis-publicaciones/${encodeURIComponent(id)}/cancelar`);
  return respuesta.data;
}
