import axios from 'axios';

// Cliente compartido para las llamadas al backend Spring Boot.
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
});

export default api;
