import React from 'react';
import { Navigate } from 'react-router-dom';
import { useSesion } from '../context/SesionContext.jsx';

// Esta comprobación orienta la navegación; el backend vuelve a comprobar todos los permisos.
export default function AccesoVista({ roles, children }) {
  const { usuario, rol, paginaInicial, modoVistaPrevia } = useSesion();
  if (!usuario) return modoVistaPrevia ? children : <Navigate to="/login" replace />;
  return roles.includes(rol) ? children : <Navigate to={paginaInicial} replace />;
}
