import React from 'react';
import AppRoutes from './routes/AppRoutes.jsx';
import { SesionProvider } from './context/SesionContext.jsx';

export default function App() {
  return (
    <SesionProvider><AppRoutes /></SesionProvider>
  );
}
