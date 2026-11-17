import React from 'react';
import { useLocation } from 'react-router-dom';

export default function Inicio() {
  const { state } = useLocation();
  return (
    <section aria-labelledby="titulo-inicio">
      <h1 id="titulo-inicio">Bienvenido a EmpleApp</h1>
      {state?.nombreUsuario && (
        <p role="status">
          Hola, {state.nombreUsuario}. Tus credenciales se verificaron correctamente.
        </p>
      )}
      <p>Conectamos empleadores y trabajadores.</p>
    </section>
  );
}
