import React, { useState } from 'react';
import { Search, Eye, ChevronLeft, ChevronRight, FileText } from 'lucide-react';
import { useSesion } from '../context/SesionContext.jsx';
import useDatos from '../hooks/useDatos.js';
import { publicacionesEjemplo } from '../data/ejemplos.js';
import { listarServicios } from '../services/servicioService.js';
import EstadoVacio from '../components/EstadoVacio.jsx';
import Modal from '../components/Modal.jsx';

const moneda = (valor) =>
  new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    maximumFractionDigits: 0,
  }).format(valor || 0);
const PUBLICACIONES_POR_PAGINA = 8;
export default function Publicaciones() {
  const { usuario } = useSesion();
  const { datos, cargando, error, recargar } = useDatos(
    listarServicios,
    publicacionesEjemplo,
    !usuario,
  );
  const [busqueda, setBusqueda] = useState('');
  const [estado, setEstado] = useState('');
  const [pagina, setPagina] = useState(1);
  const [seleccionado, setSeleccionado] = useState(null);
  const filas = (datos || []).filter(
    (fila) =>
      `${fila.tipoServicio?.nombreTipo || ''} ${fila.ubicacion || ''} ${fila.empleador?.usuario?.nombre || ''}`
        .toLowerCase()
        .includes(busqueda.toLowerCase()) &&
      (!estado || fila.estado?.nombreEstado === estado),
  );
  const estados = [
    ...new Set((datos || []).map((fila) => fila.estado?.nombreEstado).filter(Boolean)),
  ];
  // Primero filtramos los registros y después seleccionamos la página visible.
  const paginas = Math.max(1, Math.ceil(filas.length / PUBLICACIONES_POR_PAGINA));
  const actual = Math.min(pagina, paginas);
  const visibles = filas.slice(
    (actual - 1) * PUBLICACIONES_POR_PAGINA,
    actual * PUBLICACIONES_POR_PAGINA,
  );
  return (
    <>
      <div className="titulo-pagina">
        <div>
          <p className="eyebrow">OPORTUNIDADES QUE CONECTAN</p>
          <h1>Gestión de publicaciones</h1>
          <p>Consulta los servicios publicados en la plataforma.</p>
        </div>
        <span className="icono-color violeta">
          <FileText size={26} />
        </span>
      </div>
      <section className="tarjeta tabla-tarjeta">
        <div className="filtros">
          <label className="buscar">
            <Search size={18} />
            <input
              aria-label="Buscar publicaciones"
              placeholder="Buscar servicio, empleador o ubicación…"
              value={busqueda}
              onChange={(e) => {
                setBusqueda(e.target.value);
                setPagina(1);
              }}
            />
          </label>
          <select
            aria-label="Filtrar por estado"
            value={estado}
            onChange={(e) => {
              setEstado(e.target.value);
              setPagina(1);
            }}
          >
            <option value="">Todos los estados</option>
            {estados.map((nombre) => (
              <option key={nombre}>{nombre}</option>
            ))}
          </select>
        </div>
        {error && (
          <div role="alert" className="aviso error">
            {error} <button onClick={recargar}>Reintentar</button>
          </div>
        )}
        <div className="tabla-scroll">
          <table>
            <caption className="sr-only">Publicaciones de servicios</caption>
            <thead>
              <tr>
                <th>ID</th>
                <th>Servicio</th>
                <th>Empleador</th>
                <th>Ubicación</th>
                <th>Valor ofertado</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {visibles.map((fila) => (
                <tr key={fila.idServicio}>
                  <td>#{fila.idServicio}</td>
                  <td>
                    <strong>{fila.tipoServicio?.nombreTipo}</strong>
                    <small>{fila.fecha}</small>
                  </td>
                  <td>{fila.empleador?.usuario?.nombre}</td>
                  <td>{fila.ubicacion}</td>
                  <td>{moneda(fila.valorOfertado)}</td>
                  <td>
                    <span className="badge">{fila.estado?.nombreEstado || 'Sin estado'}</span>
                  </td>
                  <td>
                    <button
                      className="boton-icono"
                      aria-label={`Ver publicación ${fila.idServicio}`}
                      onClick={() => setSeleccionado(fila)}
                    >
                      <Eye size={18} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {cargando ? (
          <p className="cargando" role="status">
            Cargando publicaciones…
          </p>
        ) : (
          !error &&
          filas.length === 0 && (
            <EstadoVacio
              titulo={
                busqueda || estado ? 'No encontramos coincidencias' : 'Aún no hay publicaciones'
              }
              texto={
                busqueda || estado
                  ? 'Prueba con otra búsqueda o cambia el estado.'
                  : 'Los servicios publicados aparecerán aquí.'
              }
            />
          )
        )}
        <div className="paginacion">
          <span>
            {filas.length
              ? `${(actual - 1) * PUBLICACIONES_POR_PAGINA + 1}–${Math.min(actual * PUBLICACIONES_POR_PAGINA, filas.length)}`
              : '0'}{' '}
            de {filas.length} publicaciones
          </span>
          <div>
            <button
              className="boton secundario compacto"
              disabled={actual <= 1}
              onClick={() => setPagina(actual - 1)}
            >
              <ChevronLeft size={16} />
              Anterior
            </button>
            <span className="numero-pagina" aria-current="page">
              {actual}
            </span>
            <button
              className="boton secundario compacto"
              disabled={actual >= paginas}
              onClick={() => setPagina(actual + 1)}
            >
              Siguiente
              <ChevronRight size={16} />
            </button>
          </div>
        </div>
      </section>
      <p className="nota-pagina">
        Esta primera vista permite consultar publicaciones. El formulario para crear ofertas se
        incorporará en un siguiente paso.
      </p>
      {seleccionado && (
        <Modal
          titulo={`Publicación #${seleccionado.idServicio}`}
          onCerrar={() => setSeleccionado(null)}
        >
          <dl className="datos-lista">
            <div>
              <dt>Servicio</dt>
              <dd>{seleccionado.tipoServicio?.nombreTipo}</dd>
            </div>
            <div>
              <dt>Ubicación</dt>
              <dd>{seleccionado.ubicacion}</dd>
            </div>
            <div>
              <dt>Fecha</dt>
              <dd>{seleccionado.fecha}</dd>
            </div>
            <div>
              <dt>Horario</dt>
              <dd>
                {seleccionado.horaInicio} – {seleccionado.horaFin}
              </dd>
            </div>
            <div>
              <dt>Valor ofertado</dt>
              <dd>{moneda(seleccionado.valorOfertado)}</dd>
            </div>
            <div>
              <dt>Estado</dt>
              <dd>{seleccionado.estado?.nombreEstado}</dd>
            </div>
          </dl>
        </Modal>
      )}
    </>
  );
}
