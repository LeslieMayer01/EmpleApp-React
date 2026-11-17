import { useEffect, useState } from 'react';
export default function useDatos(cargar, ejemplo, vistaPrevia) {
  const [resultado, setResultado] = useState({ datos: ejemplo, cargando: !vistaPrevia, error: '' });
  const [intento, setIntento] = useState(0);
  useEffect(() => {
    let activo = true;
    if (vistaPrevia) { setResultado({ datos: ejemplo, cargando: false, error: '' }); return () => { activo = false; }; }
    setResultado({ datos: null, cargando: true, error: '' });
    cargar().then(datos => { if (activo) setResultado({ datos, cargando: false, error: '' }); })
      .catch(() => { if (activo) setResultado({ datos: null, cargando: false, error: 'No pudimos cargar los datos. Comprueba la conexión con el backend.' }); });
    return () => { activo = false; };
  }, [cargar, ejemplo, vistaPrevia, intento]);
  return { ...resultado, recargar: () => setIntento(valor => valor + 1) };
}
