import React, { useEffect, useRef } from 'react';
import { X } from 'lucide-react';
export default function Modal({ titulo, onCerrar, children }) {
  const ref = useRef(null);
  // El diálogo nativo administra el foco y bloquea el contenido que queda detrás.
  useEffect(() => {
    ref.current.showModal();
  }, []);
  return (
    <dialog
      ref={ref}
      className="modal"
      aria-labelledby="titulo-modal"
      onCancel={(evento) => {
        evento.preventDefault();
        onCerrar();
      }}
      onClick={(evento) => {
        if (evento.target === ref.current) onCerrar();
      }}
    >
      <div className="cabecera-seccion">
        <h2 id="titulo-modal">{titulo}</h2>
        <button className="boton-icono" onClick={onCerrar} aria-label="Cerrar ventana">
          <X size={21} />
        </button>
      </div>
      {children}
    </dialog>
  );
}
