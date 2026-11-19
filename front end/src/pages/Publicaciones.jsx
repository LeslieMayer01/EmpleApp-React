import React, { useCallback, useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import {
  Search,
  Eye,
  ChevronLeft,
  ChevronRight,
  FileText,
  Plus,
  Pencil,
  XCircle,
} from 'lucide-react';
import { useSesion } from '../context/SesionContext.jsx';
import useDatos from '../hooks/useDatos.js';
import { publicacionesEjemplo } from '../data/ejemplos.js';
import {
  buscarPublicaciones,
  publicarServicio,
  actualizarPublicacion,
  cancelarPublicacion,
} from '../services/servicioService.js';
import { listarTiposServicio } from '../services/tipoServicioService.js';
import { listarEstadosServicio } from '../services/estadoServicioService.js';
import EstadoVacio from '../components/EstadoVacio.jsx';
import Modal from '../components/Modal.jsx';
import FormularioPublicacion from '../components/FormularioPublicacion.jsx';

const TAMANO_PAGINA = 8;
const PAGINA_EJEMPLO = {
  contenido: publicacionesEjemplo,
  pagina: 0,
  tamano: TAMANO_PAGINA,
  totalElementos: publicacionesEjemplo.length,
  totalPaginas: publicacionesEjemplo.length ? 1 : 0,
};
const FILTROS_INICIALES = {
  busqueda: '',
  tipoId: '',
  estadoId: '',
  ubicacion: '',
  fechaDesde: '',
  fechaHasta: '',
};
const moneda = (valor) =>
  new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    maximumFractionDigits: 2,
  }).format(valor || 0);

export default function Publicaciones() {
  const { usuario, rol } = useSesion();
  const [parametros] = useSearchParams();
  const empleador = rol === 'empleador';
  const [soloMias, setSoloMias] = useState(empleador && parametros.get('mis') === '1');
  const [campos, setCampos] = useState(FILTROS_INICIALES);
  const [filtros, setFiltros] = useState({});
  const [pagina, setPagina] = useState(0);
  const [tipos, setTipos] = useState([]);
  const [estados, setEstados] = useState([]);
  const [errorCatalogos, setErrorCatalogos] = useState('');
  const [cargandoCatalogos, setCargandoCatalogos] = useState(false);
  const [intentoCatalogos, setIntentoCatalogos] = useState(0);
  const [errorFiltro, setErrorFiltro] = useState('');
  const [mensaje, setMensaje] = useState('');
  const [detalle, setDetalle] = useState(null);
  const [formulario, setFormulario] = useState(empleador && parametros.get('crear') === '1');
  const [edicion, setEdicion] = useState(null);
  const [cancelacion, setCancelacion] = useState(null);
  const [enviando, setEnviando] = useState(false);
  const [errorCancelar, setErrorCancelar] = useState('');

  const cargar = useCallback(
    () => buscarPublicaciones({ ...filtros, pagina, tamano: TAMANO_PAGINA }, soloMias),
    [filtros, pagina, soloMias],
  );
  const { datos, cargando, error, recargar } = useDatos(cargar, PAGINA_EJEMPLO, !usuario);
  const filas = datos?.contenido || [];
  const total = datos?.totalElementos || 0;
  const paginas = datos?.totalPaginas || 0;

  // Los catálogos son independientes de la búsqueda; no se reconstruyen desde una sola página.
  useEffect(() => {
    if (!usuario) return;
    let activo = true;
    setCargandoCatalogos(true);
    setErrorCatalogos('');
    Promise.all([listarTiposServicio(), listarEstadosServicio()])
      .then(([tiposRecibidos, estadosRecibidos]) => {
        if (activo) {
          setTipos(tiposRecibidos);
          setEstados(estadosRecibidos);
        }
      })
      .catch(() => {
        if (activo) setErrorCatalogos('No se pudieron cargar los tipos y estados de servicio.');
      })
      .finally(() => {
        if (activo) setCargandoCatalogos(false);
      });
    return () => {
      activo = false;
    };
  }, [usuario?.idUsuario, intentoCatalogos]);

  useEffect(() => {
    if (!cargando && datos && pagina > 0 && pagina >= datos.totalPaginas)
      setPagina(Math.max(0, datos.totalPaginas - 1));
  }, [datos, pagina, cargando]);

  const filtrar = (evento) => {
    evento.preventDefault();
    setErrorFiltro('');
    if (campos.fechaDesde && campos.fechaHasta && campos.fechaDesde > campos.fechaHasta) {
      setErrorFiltro('La fecha inicial no puede ser posterior a la final.');
      return;
    }
    const valores = Object.fromEntries(
      Object.entries(campos)
        .filter(([, valor]) => valor !== '')
        .map(([clave, valor]) => [
          clave,
          ['tipoId', 'estadoId'].includes(clave) ? Number(valor) : valor.trim(),
        ]),
    );
    setFiltros(valores);
    setPagina(0);
  };
  const cambiarFiltro = (evento) =>
    setCampos((actual) => ({ ...actual, [evento.target.name]: evento.target.value }));
  const cambiarVista = (propias) => {
    setSoloMias(propias);
    setPagina(0);
    setMensaje('');
  };
  const abrirFormulario = (publicacion) => {
    setEdicion(publicacion);
    setFormulario(true);
    setMensaje('');
  };
  const guardar = async (datosFormulario) => {
    setEnviando(true);
    try {
      if (edicion) await actualizarPublicacion(edicion.idServicio, datosFormulario);
      else await publicarServicio(datosFormulario);
      setMensaje(
        edicion
          ? 'Publicación actualizada correctamente.'
          : 'Tu servicio fue publicado correctamente.',
      );
      setFormulario(false);
      setEdicion(null);
      setSoloMias(true);
      setPagina(0);
      recargar();
      setIntentoCatalogos((actual) => actual + 1);
    } finally {
      setEnviando(false);
    }
  };
  const confirmarCancelacion = async () => {
    setEnviando(true);
    setErrorCancelar('');
    try {
      await cancelarPublicacion(cancelacion.idServicio);
      setCancelacion(null);
      setMensaje('La publicación fue cancelada. Se conserva su información.');
      recargar();
      setIntentoCatalogos((actual) => actual + 1);
    } catch (fallo) {
      setErrorCancelar(
        fallo.response?.status === 409
          ? 'La oferta cambió de estado o tiene postulaciones o contrataciones vinculadas.'
          : 'No se pudo cancelar. Comprueba tus permisos y la conexión.',
      );
    } finally {
      setEnviando(false);
    }
  };

  return (
    <>
      <div className="titulo-pagina">
        <div>
          <p className="eyebrow">OPORTUNIDADES QUE CONECTAN</p>
          <h1>{soloMias ? 'Mis publicaciones' : 'Publicaciones de servicios'}</h1>
          <p>
            {soloMias
              ? 'Administra las ofertas publicadas a tu nombre.'
              : 'Encuentra servicios por tipo, ubicación y fecha.'}
          </p>
        </div>
        {empleador ? (
          <button className="boton" onClick={() => abrirFormulario(null)}>
            <Plus size={17} />
            Nueva publicación
          </button>
        ) : (
          <span className="icono-color violeta">
            <FileText size={26} />
          </span>
        )}
      </div>
      {empleador && (
        <div className="pestanas-publicaciones" role="group" aria-label="Alcance de publicaciones">
          <button aria-pressed={!soloMias} onClick={() => cambiarVista(false)}>
            Todas las publicaciones
          </button>
          <button aria-pressed={soloMias} onClick={() => cambiarVista(true)}>
            Mis publicaciones
          </button>
        </div>
      )}
      {mensaje && (
        <p className="aviso" role="status">
          {mensaje}
        </p>
      )}
      {errorCatalogos && (
        <div className="aviso error" role="alert">
          {errorCatalogos}
          <button onClick={() => setIntentoCatalogos((actual) => actual + 1)}>Reintentar</button>
        </div>
      )}
      <section className="tarjeta tabla-tarjeta">
        <form className="filtros filtros-publicaciones" onSubmit={filtrar}>
          <label className="buscar">
            <Search size={18} />
            <input
              name="busqueda"
              aria-label="Buscar publicaciones"
              maxLength={100}
              placeholder="Servicio, empleador o ubicación…"
              value={campos.busqueda}
              onChange={cambiarFiltro}
            />
          </label>
          <label>
            Tipo
            <select
              aria-label="Filtrar por tipo"
              name="tipoId"
              value={campos.tipoId}
              onChange={cambiarFiltro}
            >
              <option value="">Todos los tipos</option>
              {tipos.map((tipo) => (
                <option key={tipo.idTipoServicio} value={tipo.idTipoServicio}>
                  {tipo.nombreTipo}
                </option>
              ))}
            </select>
          </label>
          <label>
            Estado
            <select
              aria-label="Filtrar por estado"
              name="estadoId"
              value={campos.estadoId}
              onChange={cambiarFiltro}
            >
              <option value="">Todos los estados</option>
              {estados.map((estado) => (
                <option key={estado.idEstado} value={estado.idEstado}>
                  {estado.nombreEstado}
                </option>
              ))}
            </select>
          </label>
          <label>
            Ubicación
            <input
              name="ubicacion"
              maxLength={255}
              placeholder="Ciudad o sector"
              value={campos.ubicacion}
              onChange={cambiarFiltro}
            />
          </label>
          <label>
            Desde
            <input
              type="date"
              name="fechaDesde"
              value={campos.fechaDesde}
              onChange={cambiarFiltro}
            />
          </label>
          <label>
            Hasta
            <input
              type="date"
              name="fechaHasta"
              value={campos.fechaHasta}
              onChange={cambiarFiltro}
            />
          </label>
          <div className="acciones-filtro">
            <button className="boton compacto" type="submit" disabled={cargando || !usuario}>
              Buscar
            </button>
            <button
              className="boton secundario compacto"
              type="button"
              onClick={() => {
                setCampos(FILTROS_INICIALES);
                setFiltros({});
                setPagina(0);
                setErrorFiltro('');
              }}
            >
              Limpiar
            </button>
          </div>
        </form>
        {errorFiltro && (
          <p role="alert" className="error-texto">
            {errorFiltro}
          </p>
        )}
        {error && (
          <div role="alert" className="aviso error">
            {error}
            <button onClick={recargar}>Reintentar</button>
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
              {filas.map((fila) => (
                <tr key={fila.idServicio}>
                  <td>#{fila.idServicio}</td>
                  <td>
                    <strong>{fila.tipoServicio.nombreTipo}</strong>
                    <small>{fila.fecha}</small>
                  </td>
                  <td>{fila.empleador.usuario.nombre}</td>
                  <td>{fila.ubicacion}</td>
                  <td>{moneda(fila.valorOfertado)}</td>
                  <td>
                    <span className="badge">{fila.estado.nombreEstado}</span>
                  </td>
                  <td>
                    <div className="acciones-tabla">
                      <button
                        className="boton-icono"
                        aria-label={`Ver publicación ${fila.idServicio}`}
                        onClick={() => setDetalle(fila)}
                      >
                        <Eye size={17} />
                      </button>
                      {soloMias && fila.estado.nombreEstado.toLowerCase() === 'publicado' && (
                        <>
                          <button
                            className="boton-icono"
                            aria-label={`Editar publicación ${fila.idServicio}`}
                            onClick={() => abrirFormulario(fila)}
                          >
                            <Pencil size={17} />
                          </button>
                          <button
                            className="boton-icono"
                            aria-label={`Cancelar publicación ${fila.idServicio}`}
                            onClick={() => {
                              setCancelacion(fila);
                              setErrorCancelar('');
                            }}
                          >
                            <XCircle size={17} />
                          </button>
                        </>
                      )}
                    </div>
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
          !filas.length && (
            <EstadoVacio
              titulo="No hay publicaciones para mostrar"
              texto={
                soloMias
                  ? 'Publica tu primer servicio o cambia los filtros de búsqueda.'
                  : 'Prueba con otros filtros o vuelve cuando haya nuevas ofertas.'
              }
            />
          )
        )}
        <div className="paginacion">
          <span>
            {total
              ? `${pagina * TAMANO_PAGINA + 1}–${Math.min((pagina + 1) * TAMANO_PAGINA, total)}`
              : '0'}{' '}
            de {total} publicaciones
          </span>
          <div>
            <button
              className="boton secundario compacto"
              disabled={!pagina || cargando}
              onClick={() => setPagina((actual) => actual - 1)}
            >
              <ChevronLeft size={16} />
              Anterior
            </button>
            <span className="numero-pagina" aria-current="page">
              {pagina + 1}
            </span>
            <button
              className="boton secundario compacto"
              disabled={pagina + 1 >= paginas || cargando}
              onClick={() => setPagina((actual) => actual + 1)}
            >
              Siguiente
              <ChevronRight size={16} />
            </button>
          </div>
        </div>
      </section>
      {!usuario && (
        <p className="nota-pagina">
          Estás en vista previa. Inicia sesión para consultar y publicar servicios reales.
        </p>
      )}
      {detalle && (
        <Modal titulo={`Publicación #${detalle.idServicio}`} onCerrar={() => setDetalle(null)}>
          <dl className="datos-lista">
            <div>
              <dt>Servicio</dt>
              <dd>{detalle.tipoServicio.nombreTipo}</dd>
            </div>
            <div>
              <dt>Empleador</dt>
              <dd>{detalle.empleador.usuario.nombre}</dd>
            </div>
            <div>
              <dt>Ubicación</dt>
              <dd>{detalle.ubicacion}</dd>
            </div>
            <div>
              <dt>Fecha</dt>
              <dd>{detalle.fecha}</dd>
            </div>
            <div>
              <dt>Horario</dt>
              <dd>
                {detalle.horaInicio} – {detalle.horaFin}
              </dd>
            </div>
            <div>
              <dt>Valor ofertado</dt>
              <dd>{moneda(detalle.valorOfertado)}</dd>
            </div>
            <div>
              <dt>Estado</dt>
              <dd>{detalle.estado.nombreEstado}</dd>
            </div>
          </dl>
        </Modal>
      )}
      {formulario && (
        <Modal
          titulo={edicion ? 'Editar publicación' : 'Publicar un servicio'}
          onCerrar={() => {
            if (!enviando) setFormulario(false);
          }}
        >
          {cargandoCatalogos ? (
            <p role="status">Cargando tipos de servicio…</p>
          ) : errorCatalogos ? (
            <p role="alert">{errorCatalogos}</p>
          ) : !tipos.length ? (
            <p>
              No hay tipos de servicio registrados. Solicita al administrador que configure el
              catálogo.
            </p>
          ) : (
            <FormularioPublicacion publicacion={edicion} tipos={tipos} onGuardar={guardar} />
          )}
        </Modal>
      )}
      {cancelacion && (
        <Modal
          titulo="Cancelar publicación"
          onCerrar={() => {
            if (!enviando) setCancelacion(null);
          }}
        >
          <p>
            ¿Quieres cancelar la publicación #{cancelacion.idServicio}? Se conservará su
            información, pero no podrás volver a editarla desde esta pantalla.
          </p>
          {errorCancelar && (
            <p role="alert" className="error-texto">
              {errorCancelar}
            </p>
          )}
          <div className="acciones-tabla">
            <button
              className="boton secundario"
              disabled={enviando}
              onClick={() => setCancelacion(null)}
            >
              Conservar publicación
            </button>
            <button className="boton" disabled={enviando} onClick={confirmarCancelacion}>
              {enviando ? 'Cancelando…' : 'Confirmar cancelación'}
            </button>
          </div>
        </Modal>
      )}
    </>
  );
}
