import React, { useCallback, useState } from 'react';
import { Link } from 'react-router-dom';
import { UserRound, Mail, MapPin, BriefcaseBusiness, MessageCircle, Megaphone, ArrowUpRight, Pencil, Clock3 } from 'lucide-react';
import { useSesion } from '../context/SesionContext.jsx';
import useDatos from '../hooks/useDatos.js';
import { perfilesEjemplo } from '../data/ejemplos.js';
import { listarTrabajadores, actualizarTrabajador } from '../services/trabajadorService.js';
import { listarEmpleadores, actualizarEmpleador } from '../services/empleadorService.js';
import Modal from '../components/Modal.jsx';
import EstadoVacio from '../components/EstadoVacio.jsx';

export default function Perfil({ tipo }) {
  const { usuario } = useSesion();
  const trabajador = tipo === 'trabajador';
  const cargar = useCallback(async () => {
    const registros = await (trabajador ? listarTrabajadores() : listarEmpleadores());
    return registros.find(registro => registro.usuario?.idUsuario === usuario?.idUsuario) || null;
  }, [trabajador, usuario?.idUsuario]);
  const { datos, cargando, error, recargar } = useDatos(cargar, perfilesEjemplo[tipo], !usuario);
  const [edicion, setEdicion] = useState(null);
  const [local, setLocal] = useState(null);
  const [guardando, setGuardando] = useState(false);
  const [mensaje, setMensaje] = useState('');
  const perfil = local || datos;
  const persona = usuario || perfil?.usuario;
  const guardar = async evento => {
    evento.preventDefault(); setGuardando(true); setMensaje('');
    try {
      if (usuario) {
        const id = trabajador ? perfil.idTrabajador : perfil.idEmpleador;
        const actualizar = trabajador ? actualizarTrabajador : actualizarEmpleador;
        await actualizar(id, { ...perfil, ...edicion }); recargar();
      } else { setLocal({ ...perfil, ...edicion }); }
      setEdicion(null); setMensaje(usuario ? 'Perfil actualizado correctamente.' : 'Cambios de ejemplo aplicados solo a esta vista previa.');
    } catch { setMensaje('No se pudo guardar el perfil. Inténtalo de nuevo.'); }
    finally { setGuardando(false); }
  };
  return <>
    <div className="titulo-pagina"><div><p className="eyebrow">TU ESPACIO EN EMPLEAPP</p><h1>Mi perfil</h1><p>{trabajador ? 'Tu experiencia abre nuevas oportunidades.' : 'Encuentra el talento para lo que necesitas.'}</p></div><span className="badge">{trabajador ? 'Trabajador' : 'Empleador'}</span></div>
    {error && <div role="alert" className="aviso error">{error} <button onClick={recargar}>Reintentar</button></div>}
    {cargando && <p role="status">Cargando perfil…</p>}
    {!cargando && !error && !perfil && <div className="aviso">Tu cuenta todavía no tiene un perfil de {tipo} asociado.</div>}
    {mensaje && !edicion && <p className="aviso" role="status">{mensaje}</p>}
    <div className="perfil-grid"><div className="perfil-columna">
      <section className="tarjeta perfil-presentacion"><div className="perfil-identidad"><span className="avatar claro"><UserRound size={29} /></span><div><h2>{persona?.nombre || 'Tu nombre'}</h2><p className="especialidad">{perfil?.especialidad || (trabajador ? 'Perfil profesional' : 'Empleador')}</p><p className="dato-icono"><MapPin size={14} />{perfil?.direccion || 'Ubicación sin registrar'}</p><p className="dato-icono"><Mail size={14} />{persona?.correo || 'Correo sin registrar'}</p></div></div><button className="boton secundario compacto" disabled={!perfil || cargando} onClick={() => { setMensaje(''); setEdicion(trabajador ? { experiencia: perfil.experiencia || '', disponibilidad: perfil.disponibilidad || '' } : { telefono: perfil.telefono || '', direccion: perfil.direccion || '' }); }}><Pencil size={14} />Editar perfil</button></section>
      <section className="tarjeta informacion"><h2>Información personal</h2><dl className="datos-lista">{trabajador ? <><div><dt>Disponibilidad</dt><dd>{perfil?.disponibilidad || 'Sin registrar'}</dd></div><div><dt>Sobre mí</dt><dd>{perfil?.experiencia || 'Agrega tu experiencia para completar tu perfil.'}</dd></div></> : <><div><dt>Teléfono</dt><dd>{perfil?.telefono || 'Sin registrar'}</dd></div><div><dt>Dirección</dt><dd>{perfil?.direccion || 'Sin registrar'}</dd></div></>}<div><dt>Correo</dt><dd>{persona?.correo || 'Sin registrar'}</dd></div></dl></section>
    </div><div className="perfil-columna">
      {!trabajador && <article className="tarjeta accion-perfil"><span className="icono-color morado"><Megaphone size={23} /></span><div><h2>Publicar una oferta</h2><p>Una nueva oportunidad para conectar con talento.</p><span className="mini-etiqueta">Formulario próximamente</span></div></article>}
      <article className="tarjeta accion-perfil"><span className="icono-color azul"><BriefcaseBusiness size={23} /></span><div><h2>Servicios disponibles</h2><p>Explora las oportunidades publicadas en EmpleApp.</p><Link className="boton azul-boton compacto" to="/admin/publicaciones">Ver servicios <ArrowUpRight size={15} /></Link></div></article>
      <article className="tarjeta accion-perfil"><span className="icono-color rosa"><MessageCircle size={23} /></span><div><h2>Mensajes</h2><p>Tu próximo contacto empieza con una conversación.</p><span className="mini-etiqueta">Mensajería próximamente</span></div></article>
    </div></div>
    <section className="tarjeta actividad"><div className="cabecera-seccion"><h2>Actividad reciente</h2><Clock3 size={19} /></div><EstadoVacio titulo="Tu historia comienza aquí" texto="El historial de actividad se incorporará en un siguiente paso." /></section>
    {edicion && <Modal titulo="Editar perfil" onCerrar={() => { if (!guardando) setEdicion(null); }}><form className="formulario" onSubmit={guardar}>{Object.keys(edicion).map(campo => <label key={campo}>{({ experiencia: 'Sobre mí', disponibilidad: 'Disponibilidad', telefono: 'Teléfono', direccion: 'Dirección' })[campo]}{campo === 'experiencia' ? <textarea value={edicion[campo]} onChange={e => setEdicion({ ...edicion, [campo]: e.target.value })} rows={4} /> : <input type={campo === 'telefono' ? 'tel' : 'text'} maxLength={campo === 'telefono' ? 20 : campo === 'direccion' ? 255 : 100} value={edicion[campo]} onChange={e => setEdicion({ ...edicion, [campo]: e.target.value })} />}</label>)}{mensaje && <p role="alert" className="error-texto">{mensaje}</p>}<button className="boton" disabled={guardando}>{guardando ? 'Guardando…' : 'Guardar cambios'}</button></form></Modal>}
  </>;
}
