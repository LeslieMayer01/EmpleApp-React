import { useEffect, useState } from 'react';

/**
 * Centraliza la carga, los errores y los reintentos de una consulta.
 * En vista previa devuelve los ejemplos sin realizar peticiones al backend.
 * La función cargar y los ejemplos deben conservar su referencia entre renderizados.
 */
export default function useDatos(cargar, ejemplo, vistaPrevia) {
  const [resultado, setResultado] = useState({ datos: ejemplo, cargando: !vistaPrevia, error: '' });
  const [intento, setIntento] = useState(0);
  useEffect(() => {
    // Evita actualizar una página que ya se cerró o que inició una consulta más reciente.
    let activo = true;
    if (vistaPrevia) {
      setResultado({ datos: ejemplo, cargando: false, error: '' });
      return () => {
        activo = false;
      };
    }
    setResultado({ datos: null, cargando: true, error: '' });
    cargar()
      .then((datos) => {
        if (activo) setResultado({ datos, cargando: false, error: '' });
      })
      .catch(() => {
        if (activo)
          setResultado({
            datos: null,
            cargando: false,
            error: 'No pudimos cargar los datos. Comprueba la conexión con el backend.',
          });
      });
    return () => {
      activo = false;
    };
  }, [cargar, ejemplo, vistaPrevia, intento]);
  // Cambiar intento vuelve a ejecutar el efecto con la misma consulta.
  return { ...resultado, recargar: () => setIntento((valor) => valor + 1) };
}
