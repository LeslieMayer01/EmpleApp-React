import React, { useState } from 'react';

export default function FormularioPublicacion({ publicacion, tipos, onGuardar }) {
  const [datos, setDatos] = useState({
    idTipoServicio: publicacion?.tipoServicio?.idTipoServicio || '',
    fecha: publicacion?.fecha || '',
    horaInicio: publicacion?.horaInicio?.slice(0, 5) || '',
    horaFin: publicacion?.horaFin?.slice(0, 5) || '',
    ubicacion: publicacion?.ubicacion || '',
    valorOfertado: publicacion?.valorOfertado ?? '',
  });
  const [guardando, setGuardando] = useState(false);
  const [error, setError] = useState('');
  const hoy = new Date();
  const fechaMinima = `${hoy.getFullYear()}-${String(hoy.getMonth() + 1).padStart(2, '0')}-${String(hoy.getDate()).padStart(2, '0')}`;
  const cambiar = (evento) => {
    setDatos((actual) => ({ ...actual, [evento.target.name]: evento.target.value }));
    setError('');
  };

  const enviar = async (evento) => {
    evento.preventDefault();
    if (guardando) return;
    if (!datos.ubicacion.trim()) {
      setError('Escribe una ubicación válida.');
      return;
    }
    if (datos.horaFin <= datos.horaInicio) {
      setError('La hora final debe ser posterior a la inicial del mismo día.');
      return;
    }
    setGuardando(true);
    setError('');
    try {
      // No enviamos ID de empleador ni estado, aunque el formulario sea de edición.
      await onGuardar({
        ...datos,
        idTipoServicio: Number(datos.idTipoServicio),
        ubicacion: datos.ubicacion.trim(),
        valorOfertado: Number(datos.valorOfertado),
      });
    } catch (fallo) {
      const estado = fallo.response?.status;
      setError(
        estado === 409
          ? 'La oferta ya no puede modificarse porque cambió de estado o tiene registros vinculados.'
          : estado === 403
            ? 'No tienes permiso para modificar esta oferta.'
            : estado === 404
              ? 'La oferta o el tipo de servicio ya no están disponibles.'
              : estado === 400
                ? 'Revisa la fecha, el horario y el valor ofertado.'
                : 'No se pudo guardar la publicación. Comprueba la conexión e inténtalo de nuevo.',
      );
    } finally {
      setGuardando(false);
    }
  };

  return (
    <form className="formulario formulario-publicacion" onSubmit={enviar} aria-busy={guardando}>
      <fieldset disabled={guardando}>
        <label htmlFor="tipo-oferta">
          Tipo de servicio
          <select
            id="tipo-oferta"
            name="idTipoServicio"
            required
            value={datos.idTipoServicio}
            onChange={cambiar}
          >
            <option value="">Selecciona un tipo</option>
            {tipos.map((tipo) => (
              <option key={tipo.idTipoServicio} value={tipo.idTipoServicio}>
                {tipo.nombreTipo}
              </option>
            ))}
          </select>
        </label>
        <label htmlFor="fecha-oferta">
          Fecha
          <input
            id="fecha-oferta"
            name="fecha"
            type="date"
            required
            min={fechaMinima}
            value={datos.fecha}
            onChange={cambiar}
          />
        </label>
        <div className="campos-horario">
          <label htmlFor="inicio-oferta">
            Hora de inicio
            <input
              id="inicio-oferta"
              name="horaInicio"
              type="time"
              required
              value={datos.horaInicio}
              onChange={cambiar}
            />
          </label>
          <label htmlFor="fin-oferta">
            Hora de finalización
            <input
              id="fin-oferta"
              name="horaFin"
              type="time"
              required
              value={datos.horaFin}
              onChange={cambiar}
            />
          </label>
        </div>
        <label htmlFor="ubicacion-oferta">
          Ubicación del servicio
          <input
            id="ubicacion-oferta"
            name="ubicacion"
            maxLength={255}
            required
            placeholder="Ciudad, sector o dirección del servicio"
            value={datos.ubicacion}
            onChange={cambiar}
          />
        </label>
        <label htmlFor="valor-oferta">
          Valor ofertado (COP)
          <input
            id="valor-oferta"
            name="valorOfertado"
            type="number"
            min="0.01"
            max="99999999.99"
            step="0.01"
            required
            value={datos.valorOfertado}
            onChange={cambiar}
          />
        </label>
        <p className="nota-pagina">La oferta se publicará a tu nombre con estado Publicado.</p>
        {error && (
          <p className="error-texto" role="alert">
            {error}
          </p>
        )}
        <button className="boton" type="submit" disabled={!tipos.length || guardando}>
          {guardando ? 'Guardando…' : publicacion ? 'Guardar cambios' : 'Publicar servicio'}
        </button>
      </fieldset>
    </form>
  );
}
