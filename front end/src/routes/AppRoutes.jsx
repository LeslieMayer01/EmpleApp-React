import React from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import Login from '../pages/Login.jsx';
import PanelLayout from '../components/PanelLayout.jsx';
import Dashboard from '../pages/Dashboard.jsx';
import Publicaciones from '../pages/Publicaciones.jsx';
import Perfil from '../pages/Perfil.jsx';

export default function AppRoutes() {
  // Login tiene su propio diseño; las vistas internas comparten PanelLayout mediante Outlet.
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<Login />} />
      <Route element={<PanelLayout />}>
        <Route path="/admin" element={<Dashboard />} />
        <Route path="/admin/publicaciones" element={<Publicaciones />} />
        <Route path="/trabajador" element={<Perfil key="trabajador" tipo="trabajador" />} />
        <Route path="/empleador" element={<Perfil key="empleador" tipo="empleador" />} />
      </Route>
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
