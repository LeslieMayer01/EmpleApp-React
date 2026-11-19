import React, { createContext, useContext, useEffect, useState } from 'react';
import {
  cerrarSesion as salirDelBackend,
  obtenerCuentaActual,
} from '../services/usuarioService.js';

const SesionContext = createContext(null);
// Restaura la identidad desde la sesión del servidor, sin guardar contraseñas ni tokens.
export function SesionProvider({ children }) {
  const [usuario, actualizarUsuario] = useState(null);
  const [comprobando, setComprobando] = useState(true);
  const [modoVistaPrevia, setModoVistaPrevia] = useState(false);

  useEffect(() => {
    let activo = true;
    const expirar = () => {
      actualizarUsuario(null);
      setModoVistaPrevia(false);
    };
    window.addEventListener('sesion-expirada', expirar);
    obtenerCuentaActual()
      .then((cuenta) => {
        if (activo) actualizarUsuario(cuenta);
      })
      .catch(() => {
        if (activo) actualizarUsuario(null);
      })
      .finally(() => {
        if (activo) setComprobando(false);
      });
    return () => {
      activo = false;
      window.removeEventListener('sesion-expirada', expirar);
    };
  }, []);

  const setUsuario = (cuenta) => {
    actualizarUsuario(cuenta);
    setModoVistaPrevia(false);
  };
  const cerrarSesion = async () => {
    if (usuario) await salirDelBackend();
    setUsuario(null);
  };
  const nombreRol = (usuario?.rol?.nombreRol || '').trim().toLowerCase();
  const rol =
    nombreRol === 'administrador' || nombreRol.startsWith('administrador ')
      ? 'administrador'
      : nombreRol;
  const paginaInicial =
    rol === 'administrador'
      ? '/admin'
      : rol === 'empleador'
        ? '/empleador'
        : rol === 'trabajador'
          ? '/trabajador'
          : '/login';

  if (comprobando)
    return (
      <main className="login-pagina" role="status">
        Comprobando sesión…
      </main>
    );
  return (
    <SesionContext.Provider
      value={{
        usuario,
        setUsuario,
        cerrarSesion,
        rol,
        paginaInicial,
        modoVistaPrevia,
        activarVistaPrevia: () => {
          if (!usuario) setModoVistaPrevia(true);
        },
      }}
    >
      {children}
    </SesionContext.Provider>
  );
}
export function useSesion() {
  return useContext(SesionContext);
}
