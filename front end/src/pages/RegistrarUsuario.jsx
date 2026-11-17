import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  ArrowUpRight,
  BriefcaseBusiness,
  Eye,
  EyeOff,
  LockKeyhole,
  Mail,
  UserRound,
} from 'lucide-react';
import { listarRoles } from '../services/rolService.js';
import { registrarUsuario } from '../services/usuarioService.js';
import '../styles/login.css';

// confirmacion solo verifica la contraseña en el formulario; no es una columna de Usuario.
const DATOS_INICIALES = { nombre: '', correo: '', contrasena: '', confirmacion: '', idRol: '' };

export default function RegistrarUsuario() {
  const [datos, setDatos] = useState(DATOS_INICIALES);
  const [roles, setRoles] = useState([]);
  const [cargandoRoles, setCargandoRoles] = useState(true);
  const [errorRoles, setErrorRoles] = useState('');
  const [intento, setIntento] = useState(0);
  const [guardando, setGuardando] = useState(false);
  const [visible, setVisible] = useState(false);
  const [mensaje, setMensaje] = useState('');
  const navigate = useNavigate();

  // Los IDs se consultan en la base de datos; no suponemos que un rol tenga un ID fijo.
  useEffect(() => {
    // Si se abandona la página, una respuesta pendiente no debe actualizar sus estados.
    let activo = true;
    setCargandoRoles(true);
    setErrorRoles('');
    listarRoles()
      .then((registros) => {
        if (!activo) return;
        // Oculta roles administrativos; el backend vuelve a validar esta restricción.
        const permitidos = registros.filter((rol) =>
          ['trabajador', 'empleador'].includes(rol.nombreRol.trim().toLowerCase()),
        );
        setRoles(permitidos);
        if (permitidos.length === 0)
          setErrorRoles('No hay tipos de cuenta disponibles. Contacta al administrador.');
      })
      .catch(() => {
        if (activo)
          setErrorRoles(
            'No pudimos cargar los tipos de cuenta. Comprueba que el backend esté encendido.',
          );
      })
      .finally(() => {
        if (activo) setCargandoRoles(false);
      });
    return () => {
      activo = false;
    };
  }, [intento]);

  const cambiarDato = (evento) => {
    // El atributo name de cada campo identifica la propiedad que debe actualizarse.
    setDatos((actuales) => ({ ...actuales, [evento.target.name]: evento.target.value }));
    setMensaje('');
  };

  const manejarRegistro = async (evento) => {
    // Gestionamos el envío con Axios y bloqueamos envíos repetidos mientras se guarda.
    evento.preventDefault();
    if (guardando) return;
    setMensaje('');
    if (!datos.nombre.trim() || !datos.correo.trim() || !datos.contrasena.trim()) {
      setMensaje('Completa los campos sin usar únicamente espacios.');
      return;
    }
    if (datos.contrasena !== datos.confirmacion) {
      setMensaje('Las contraseñas no coinciden.');
      return;
    }
    // BCrypt admite 72 bytes, que pueden equivaler a menos de 72 caracteres con tildes o emojis.
    if (new TextEncoder().encode(datos.contrasena).length > 72) {
      setMensaje('La contraseña es demasiado larga. Usa una contraseña más corta.');
      return;
    }
    setGuardando(true);
    try {
      // El select entrega texto: convertimos el ID del rol al número esperado por Java.
      await registrarUsuario({
        nombre: datos.nombre.trim(),
        correo: datos.correo.trim(),
        contrasena: datos.contrasena,
        idRol: Number(datos.idRol),
      });
      // La confirmación nunca se envía al backend y ninguna contraseña se guarda en el navegador.
      setDatos(DATOS_INICIALES);
      navigate('/login', { replace: true, state: { registroExitoso: true } });
    } catch (error) {
      // 409 indica correo duplicado; 400/404 indican datos o rol inválidos.
      if (error.response?.status === 409)
        setMensaje('Este correo ya está registrado. Inicia sesión o utiliza otro.');
      else if ([400, 404].includes(error.response?.status))
        setMensaje('Revisa los datos y selecciona un tipo de cuenta válido.');
      else if (!error.response)
        setMensaje('No se pudo conectar con el servidor. Comprueba que el backend esté encendido.');
      else setMensaje('No pudimos crear tu cuenta. Inténtalo de nuevo.');
    } finally {
      setGuardando(false);
    }
  };

  return (
    <main className="login-pagina">
      <section className="login registro" aria-labelledby="titulo-registro">
        <aside className="login-marca">
          <div>
            <div className="brand">
              <span className="brand-icon">
                E<span>·</span>
              </span>
              EmpleApp
            </div>
            <p>
              Tu talento y tus ideas
              <br />
              merecen una oportunidad.
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
              <BriefcaseBusiness />
            </span>
            <span className="flotante tres">
              <ArrowUpRight />
            </span>
          </div>
          <small>Un espacio para encontrar y ofrecer talento.</small>
        </aside>
        <div className="login-contenido">
          <p className="eyebrow">TU PRÓXIMA CONEXIÓN EMPIEZA AQUÍ</p>
          <h1 id="titulo-registro">Crear una cuenta</h1>
          <p className="login-subtitulo">Únete a la comunidad de EmpleApp.</p>
          {/* Los límites de longitud corresponden a los campos definidos en el modelo Usuario. */}
          <form className="login-formulario" onSubmit={manejarRegistro} aria-busy={guardando}>
            <div className="login-campo">
              <label htmlFor="nombre-registro">Nombre completo</label>
              <div className="input-icono">
                <UserRound size={18} />
                <input
                  id="nombre-registro"
                  name="nombre"
                  autoComplete="name"
                  maxLength={100}
                  placeholder="Tu nombre completo"
                  value={datos.nombre}
                  onChange={cambiarDato}
                  required
                  disabled={guardando}
                />
              </div>
            </div>
            <div className="login-campo">
              <label htmlFor="correo-registro">Correo electrónico</label>
              <div className="input-icono">
                <Mail size={18} />
                <input
                  id="correo-registro"
                  name="correo"
                  type="email"
                  autoComplete="email"
                  maxLength={100}
                  placeholder="ejemplo@correo.com"
                  value={datos.correo}
                  onChange={cambiarDato}
                  required
                  disabled={guardando}
                />
              </div>
            </div>
            <div className="login-campo">
              <label htmlFor="rol-registro">Quiero registrarme como</label>
              <select
                id="rol-registro"
                name="idRol"
                value={datos.idRol}
                onChange={cambiarDato}
                required
                disabled={guardando || cargandoRoles || !!errorRoles}
              >
                <option value="">
                  {cargandoRoles ? 'Cargando tipos de cuenta…' : 'Selecciona un tipo de cuenta'}
                </option>
                {roles.map((rol) => (
                  <option key={rol.idRol} value={rol.idRol}>
                    {rol.nombreRol}
                  </option>
                ))}
              </select>
            </div>
            {errorRoles && (
              <div className="login-error" role="alert">
                {errorRoles}
                <button
                  type="button"
                  className="registro-reintentar"
                  onClick={() => setIntento((valor) => valor + 1)}
                >
                  Reintentar
                </button>
              </div>
            )}
            <div className="login-campo">
              <label htmlFor="clave-registro">Contraseña</label>
              <div className="input-icono">
                <LockKeyhole size={18} />
                <input
                  id="clave-registro"
                  name="contrasena"
                  type={visible ? 'text' : 'password'}
                  autoComplete="new-password"
                  maxLength={72}
                  placeholder="Crea tu contraseña"
                  value={datos.contrasena}
                  onChange={cambiarDato}
                  required
                  disabled={guardando}
                />
                <button
                  type="button"
                  className="ver-clave"
                  aria-label={visible ? 'Ocultar contraseñas' : 'Mostrar contraseñas'}
                  aria-pressed={visible}
                  onClick={() => setVisible(!visible)}
                >
                  {visible ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>
            <div className="login-campo">
              <label htmlFor="confirmacion-registro">Confirmar contraseña</label>
              <div className="input-icono">
                <LockKeyhole size={18} />
                <input
                  id="confirmacion-registro"
                  name="confirmacion"
                  type={visible ? 'text' : 'password'}
                  autoComplete="new-password"
                  maxLength={72}
                  placeholder="Repite tu contraseña"
                  value={datos.confirmacion}
                  onChange={cambiarDato}
                  required
                  disabled={guardando}
                />
              </div>
            </div>
            {mensaje && (
              <p className="login-error" role="alert">
                {mensaje}
              </p>
            )}
            <button
              className="boton login-enviar"
              type="submit"
              disabled={guardando || cargandoRoles || !!errorRoles}
            >
              {guardando ? 'Creando tu cuenta…' : 'Crear cuenta'}
              {!guardando && <ArrowUpRight size={17} />}
            </button>
          </form>
          <p className="registro-enlace">
            ¿Ya tienes una cuenta? <Link to="/login">Inicia sesión</Link>
          </p>
        </div>
      </section>
      <footer className="login-footer">
        © {new Date().getFullYear()} EmpleApp · Conectamos personas, creamos oportunidades.
      </footer>
    </main>
  );
}
