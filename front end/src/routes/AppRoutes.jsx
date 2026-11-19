import React from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import Login from '../pages/Login.jsx';
import RegistrarUsuario from '../pages/RegistrarUsuario.jsx';
import PanelLayout from '../components/PanelLayout.jsx';
import Dashboard from '../pages/Dashboard.jsx';
import Publicaciones from '../pages/Publicaciones.jsx';
import Perfil from '../pages/Perfil.jsx';
import AccesoVista from '../components/AccesoVista.jsx';

export default function AppRoutes() {
  // Login tiene su propio diseño; las vistas internas comparten PanelLayout mediante Outlet.
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<Login />} />
      <Route path="/registro" element={<RegistrarUsuario />} />
      <Route element={<PanelLayout />}>
        <Route
          path="/admin"
          element={
            <AccesoVista roles={['administrador']}>
              <Dashboard />
            </AccesoVista>
          }
        />
        <Route
          path="/admin/publicaciones"
          element={
            <AccesoVista roles={['administrador', 'trabajador', 'empleador']}>
              <Publicaciones />
            </AccesoVista>
          }
        />
        <Route
          path="/trabajador"
          element={
            <AccesoVista roles={['trabajador']}>
              <Perfil key="trabajador" tipo="trabajador" />
            </AccesoVista>
          }
        />
        <Route
          path="/empleador"
          element={
            <AccesoVista roles={['empleador']}>
              <Perfil key="empleador" tipo="empleador" />
            </AccesoVista>
          }
        />
      </Route>
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
