import React from 'react';
import AppRoutes from './routes/AppRoutes.jsx';
import { SesionProvider } from './context/SesionContext.jsx';

export default function App() {
  // Todas las páginas comparten el usuario que inició sesión y las rutas de la aplicación.
  return (
    <SesionProvider>
      <AppRoutes />
    </SesionProvider>
  );
}
