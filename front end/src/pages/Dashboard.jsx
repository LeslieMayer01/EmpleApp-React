import React from 'react';
import { Link } from 'react-router-dom';
import { Users, FileText, BriefcaseBusiness, CreditCard, ArrowUpRight, ChartPie } from 'lucide-react';
import { useSesion } from '../context/SesionContext.jsx';
import useDatos from '../hooks/useDatos.js';
import { resumenEjemplo } from '../data/ejemplos.js';
import { listarUsuarios } from '../services/usuarioService.js';
import { listarServicios } from '../services/servicioService.js';
import { listarPagos } from '../services/pagoService.js';
async function cargarResumen() {
  const [usuarios, servicios, pagos] = await Promise.all([listarUsuarios(), listarServicios(), listarPagos()]);
  return { usuarios: usuarios.length, publicaciones: servicios.length, servicios: servicios.length, pagos: pagos.length };
}
export default function Dashboard() {
  const { usuario } = useSesion();
  const { datos, cargando, error, recargar } = useDatos(cargarResumen, resumenEjemplo, !usuario);
  const tarjetas = [['usuarios', 'Usuarios', Users, 'azul'], ['publicaciones', 'Publicaciones', FileText, 'violeta'], ['servicios', 'Servicios', BriefcaseBusiness, 'morado'], ['pagos', 'Pagos', CreditCard, 'rosa']];
  return <>
    <div className="titulo-pagina"><div><p className="eyebrow">TU PLATAFORMA, EN UN VISTAZO</p><h1>Panel principal</h1><p>Bienvenido. Aquí comienza una nueva oportunidad.</p></div><Link className="boton secundario" to="/admin/publicaciones">Ver publicaciones <ArrowUpRight size={17} /></Link></div>
    {error && <div role="alert" className="aviso error">{error} <button onClick={recargar}>Reintentar</button></div>}
    <div className="estadisticas">{tarjetas.map(([clave, nombre, Icono, color]) => <article className="tarjeta estadistica" key={clave}><div className="stat-top"><span className={`icono-color ${color}`}><Icono size={23} /></span><span className="mini-etiqueta">Total</span></div><h2>{nombre}</h2><strong>{cargando ? '…' : datos?.[clave] ?? '—'}</strong><span className="texto-suave">Registros en la plataforma</span></article>)}</div>
    <section className="tarjeta resumen"><div className="cabecera-seccion"><div><h2>Resumen general</h2><p>Un espacio para seguir la actividad de tu comunidad.</p></div><span className="mini-etiqueta">Actividad</span></div><div className="estado-vacio"><span><ChartPie size={58} strokeWidth={1.5} /></span><h3>Todo empieza con una conexión</h3><p>Consulta las publicaciones para conocer los servicios de la plataforma.</p><Link to="/admin/publicaciones" className="enlace">Explorar publicaciones <ArrowUpRight size={15} /></Link></div></section>
    <div className="nota-pagina">Las publicaciones corresponden a los servicios ofertados en EmpleApp.</div>
  </>;
}
