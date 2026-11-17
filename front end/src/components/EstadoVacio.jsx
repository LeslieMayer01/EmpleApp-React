import React from 'react';
import { Inbox } from 'lucide-react';
export default function EstadoVacio({
  titulo = 'Aún no hay registros',
  texto = 'Los datos aparecerán aquí cuando estén disponibles.',
}) {
  return (
    <div className="estado-vacio">
      <span>
        <Inbox size={34} />
      </span>
      <h3>{titulo}</h3>
      <p>{texto}</p>
    </div>
  );
}
