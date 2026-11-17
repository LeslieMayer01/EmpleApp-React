// Datos exclusivamente visuales. Nunca se envían al backend.
export const resumenEjemplo = { usuarios: 0, publicaciones: 0, servicios: 0, pagos: 0 };
export const publicacionesEjemplo = [];
export const perfilesEjemplo = {
  trabajador: {
    usuario: { nombre: 'Ana María', correo: 'anamaria@ejemplo.com' },
    experiencia: 'Experiencia en servicios domésticos y cuidado de espacios.',
    disponibilidad: 'Lunes a viernes',
    especialidad: 'Servicios domésticos',
  },
  empleador: {
    usuario: { nombre: 'Juan Pérez', correo: 'juanperez@ejemplo.com' },
    direccion: 'Medellín, Colombia',
    telefono: '300 123 4567',
    especialidad: 'Empleador',
  },
};
