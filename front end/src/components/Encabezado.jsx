import React from 'react';
import { Link } from 'react-router-dom';

export default function Encabezado() {
  return (
    <header className="encabezado">
      <Link to="/" className="marca" aria-label="EmpleApp: inicio">
        EmpleApp
      </Link>
      <nav aria-label="Navegación principal">
        <Link to="/login">Iniciar sesión</Link>
      </nav>
    </header>
  );
}
