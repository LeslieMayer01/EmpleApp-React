import axios from 'axios';

// Cliente compartido para las llamadas al backend Spring Boot.
const api = axios.create({
  baseURL: import.meta.env?.VITE_API_URL || 'http://localhost:8080/api',
  timeout: 10000,
  // El navegador administra la cookie HTTP-only; nunca la copiamos a localStorage.
  withCredentials: true,
});

api.interceptors.request.use(async (configuracion) => {
  if (!['get', 'head', 'options'].includes((configuracion.method || 'get').toLowerCase())) {
    // Se obtiene de nuevo tras login/logout para no reutilizar un token de una sesión anterior.
    const { data } = await api.get('/usuario/csrf');
    configuracion.headers.set(data.headerName, data.token);
  }
  return configuracion;
});

api.interceptors.response.use(
  (respuesta) => respuesta,
  (error) => {
    if (
      error.response?.status === 401 &&
      error.config?.url !== '/usuario/login' &&
      typeof window !== 'undefined'
    ) {
      window.dispatchEvent(new Event('sesion-expirada'));
    }
    return Promise.reject(error);
  },
);

export default api;
