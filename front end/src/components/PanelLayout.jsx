import React, { useState } from 'react';
import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom';
import {
  BriefcaseBusiness,
  LayoutDashboard,
  FileText,
  UserRound,
  LogOut,
  Menu,
  X,
  ArrowUpRight,
} from 'lucide-react';
import { useSesion } from '../context/SesionContext.jsx';

export default function PanelLayout() {
  // Este contenedor comparte el menú y el pie de página entre las cuatro vistas internas.
  const { usuario, rol, paginaInicial, cerrarSesion } = useSesion();
  const { pathname } = useLocation();
  const navigate = useNavigate();
  const [abierto, setAbierto] = useState(false);
  const [saliendo, setSaliendo] = useState(false);
  const [errorSalida, setErrorSalida] = useState('');
  const perfil =
    pathname === '/trabajador'
      ? 'Trabajador'
      : pathname === '/empleador'
        ? 'Empleador'
        : pathname === '/admin/publicaciones'
          ? 'Publicaciones'
          : 'Administrador';
  // En vista previa se pueden recorrer los prototipos; con sesión se muestra el menú del rol.
  const enlaces = [
    ['/admin', LayoutDashboard, 'Panel principal'],
    ['/admin/publicaciones', FileText, 'Publicaciones'],
    ['/trabajador', UserRound, 'Perfil del trabajador'],
    ['/empleador', BriefcaseBusiness, 'Perfil del empleador'],
  ].filter(
    ([ruta]) =>
      !usuario ||
      ruta === '/admin/publicaciones' ||
      (rol === 'administrador' && ruta === '/admin') ||
      (rol === 'trabajador' && ruta === '/trabajador') ||
      (rol === 'empleador' && ruta === '/empleador'),
  );

  const salir = async () => {
    setErrorSalida('');
    setSaliendo(true);
    try {
      await cerrarSesion();
      navigate('/login');
    } catch {
      setErrorSalida('No se pudo cerrar la sesión. Comprueba la conexión e inténtalo de nuevo.');
    } finally {
      setSaliendo(false);
    }
  };
  return (
    <div className="panel-app">
      <a href="#contenido" className="saltar">
        Ir al contenido
      </a>
      <button
        className="menu-movil"
        onClick={() => setAbierto(!abierto)}
        aria-label={abierto ? 'Cerrar menú' : 'Abrir menú'}
        aria-expanded={abierto}
        aria-controls="menu-lateral"
      >
        {abierto ? <X /> : <Menu />} EmpleApp
      </button>
      <aside id="menu-lateral" className={`sidebar ${abierto ? 'abierto' : ''}`}>
        <NavLink to={usuario ? paginaInicial : '/admin'} className="brand">
          <span className="brand-icon">
            E<span>·</span>
          </span>
          EmpleApp
        </NavLink>
        <div className="sidebar-persona">
          <span className="avatar">
            <UserRound size={31} />
          </span>
          <strong>
            {usuario?.nombre || (perfil === 'Trabajador' ? 'Ana María' : 'Juan Pérez')}
          </strong>
          <span>{usuario?.rol?.nombreRol || perfil}</span>
        </div>
        <p className="nav-etiqueta">ESPACIO DE TRABAJO</p>
        <nav aria-label="Vistas del proyecto">
          {enlaces.map(([ruta, Icono, texto]) => (
            <NavLink key={ruta} to={ruta} end onClick={() => setAbierto(false)}>
              <Icono size={19} />
              {texto}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-nota">
          <BriefcaseBusiness size={22} />
          <strong>El talento nos conecta</strong>
          <p>Más oportunidades, nuevas conexiones.</p>
        </div>
        <button className="salir" onClick={salir} disabled={saliendo}>
          <LogOut size={18} />
          {saliendo ? 'Cerrando…' : usuario ? 'Cerrar sesión' : 'Volver al login'}
        </button>
      </aside>
      <div className="panel-cuerpo">
        <header className="barra-superior">
          <span>
            EmpleApp <span className="separador">/</span> {perfil}
          </span>
          <span className="etiqueta-entorno">
            {usuario ? 'Proyecto formativo' : 'Vista previa · Datos de ejemplo'}
          </span>
        </header>
        <main id="contenido" className="panel-contenido">
          {errorSalida && (
            <p className="aviso error" role="alert">
              {errorSalida}
            </p>
          )}
          <Outlet />
        </main>
        <footer className="panel-footer">
          <span>© {new Date().getFullYear()} EmpleApp. Conecta talento con oportunidades.</span>
          <span>
            Hecho para conectar <ArrowUpRight size={14} />
          </span>
        </footer>
      </div>
    </div>
  );
}
