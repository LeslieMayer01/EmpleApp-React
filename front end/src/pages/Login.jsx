import React, { useState } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import {
  UserRound,
  LockKeyhole,
  Eye,
  EyeOff,
  BriefcaseBusiness,
  MessageCircle,
  ArrowUpRight,
} from 'lucide-react';
import { useSesion } from '../context/SesionContext.jsx';
import { iniciarSesion } from '../services/usuarioService.js';
import '../styles/login.css';

export default function Login() {
  const [correo, setCorreo] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [mensaje, setMensaje] = useState('');
  const [cargando, setCargando] = useState(false);
  const navigate = useNavigate();
  const { usuario: cuentaActual, paginaInicial, setUsuario, activarVistaPrevia } = useSesion();
  const [visible, setVisible] = useState(false);
  const { state } = useLocation();

  const manejarLogin = async (evento) => {
    // Evita recargar la página y enviar otra solicitud mientras se está validando el login.
    evento.preventDefault();
    if (cargando) return;
    setMensaje('');
    setCargando(true);

    try {
      const usuario = await iniciarSesion(correo.trim(), contrasena);
      // La sesión queda en una cookie HTTP-only que administra el navegador.
      setContrasena('');
      setUsuario(usuario);
      // El nombre del rol decide la página inicial; no sustituye la autorización del servidor.
      const rol = (usuario.rol?.nombreRol || '').trim().toLowerCase();
      navigate(
        rol === 'administrador' || rol.startsWith('administrador ')
          ? '/admin'
          : rol === 'empleador'
            ? '/empleador'
            : rol === 'trabajador'
              ? '/trabajador'
              : '/login',
        { replace: true },
      );
    } catch (error) {
      // Separamos credenciales inválidas, datos incorrectos y fallos de conexión.
      if (error.response?.status === 401) {
        setMensaje('Correo o contraseña incorrectos.');
      } else if (error.response?.status === 400) {
        setMensaje('Revisa el correo y la contraseña ingresados.');
      } else if (!error.response) {
        setMensaje('No se pudo conectar con el servidor. Comprueba que el backend esté encendido.');
      } else {
        setMensaje('No se pudo iniciar sesión. Inténtalo de nuevo.');
      }
    } finally {
      setCargando(false);
    }
  };

  if (cuentaActual && paginaInicial !== '/login') return <Navigate to={paginaInicial} replace />;
  return (
    <main className="login-pagina">
      <section className="login" aria-labelledby="titulo-login">
        <aside className="login-marca">
          <div>
            <div className="brand">
              <span className="brand-icon">
                E<span>·</span>
              </span>
              EmpleApp
            </div>
            <p>
              Conecta talento
              <br />
              con oportunidades.
            </p>
          </div>
          <div className="login-ilustracion" aria-hidden="true">
            <div className="orbita">
              <UserRound size={90} strokeWidth={1} />
            </div>
            <span className="flotante uno">
              <UserRound />
            </span>
            <span className="flotante dos">
              <MessageCircle />
            </span>
            <span className="flotante tres">
              <BriefcaseBusiness />
            </span>
          </div>
          <small>Un nuevo comienzo está a una conexión.</small>
        </aside>
        <div className="login-contenido">
          <p className="eyebrow">QUÉ BUENO TENERTE AQUÍ</p>
          <h1 id="titulo-login">Iniciar sesión</h1>
          <p className="login-subtitulo">Tu próxima oportunidad te espera.</p>
          {cuentaActual && paginaInicial === '/login' && (
            <p className="login-error" role="alert">
              Tu cuenta no tiene un rol habilitado. Contacta al administrador o ingresa con otra
              cuenta.
            </p>
          )}
          {/* El registro redirige aquí con una confirmación, sin iniciar sesión automáticamente. */}
          {state?.registroExitoso && (
            <p className="registro-exito" role="status">
              Tu cuenta fue creada. Ya puedes iniciar sesión.
            </p>
          )}

          <form onSubmit={manejarLogin} className="login-formulario" aria-busy={cargando}>
            <div className="login-campo">
              <label htmlFor="correo">Correo electrónico</label>
              <div className="input-icono">
                <UserRound size={18} />
                <input
                  id="correo"
                  name="correo"
                  type="email"
                  autoComplete="username"
                  maxLength={100}
                  value={correo}
                  onChange={(evento) => setCorreo(evento.target.value)}
                  disabled={cargando}
                  required
                  placeholder="ejemplo@correo.com"
                />
              </div>
            </div>

            <div className="login-campo">
              <label htmlFor="contrasena">Contraseña</label>
              <div className="input-icono">
                <LockKeyhole size={18} />
                <input
                  id="contrasena"
                  name="contrasena"
                  type={visible ? 'text' : 'password'}
                  autoComplete="current-password"
                  maxLength={72}
                  value={contrasena}
                  onChange={(evento) => setContrasena(evento.target.value)}
                  disabled={cargando}
                  required
                  placeholder="Tu contraseña"
                />
                <button
                  type="button"
                  className="ver-clave"
                  aria-label={visible ? 'Ocultar contraseña' : 'Mostrar contraseña'}
                  aria-pressed={visible}
                  onClick={() => setVisible(!visible)}
                >
                  {visible ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            {mensaje && (
              <p className="login-error" role="alert">
                {mensaje}
              </p>
            )}

            <button className="boton login-enviar" type="submit" disabled={cargando}>
              {cargando ? 'Ingresando…' : 'Iniciar sesión'}{' '}
              {!cargando && <ArrowUpRight size={17} />}
            </button>
          </form>
          <p className="registro-enlace">
            ¿No tienes una cuenta? <Link to="/registro">Regístrate aquí</Link>
          </p>
          <p className="login-pendiente">Recuperación de contraseña: próximamente.</p>
          <div className="login-preview">
            <span>¿Quieres conocer EmpleApp?</span>
            <Link to="/admin" onClick={activarVistaPrevia}>
              Explorar las vistas <ArrowUpRight size={14} />
            </Link>
          </div>
        </div>
      </section>
      <footer className="login-footer">
        © {new Date().getFullYear()} EmpleApp · Conectamos personas, creamos oportunidades.
      </footer>
    </main>
  );
}
