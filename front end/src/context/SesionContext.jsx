import React, { createContext, useContext, useState } from 'react';

const SesionContext = createContext(null);
// Estado de navegación en memoria; la autorización debe realizarla el backend.
export function SesionProvider({ children }) {
  const [usuario, setUsuario] = useState(null);
  return (
    <SesionContext.Provider value={{ usuario, setUsuario }}>{children}</SesionContext.Provider>
  );
}
export function useSesion() {
  return useContext(SesionContext);
}
