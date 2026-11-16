document.addEventListener('DOMContentLoaded', () => {
  const importe = document.getElementById('valorServicio');
  if (importe) {
    const mostrar = () => {
      const partes = /^(\d{1,8})(?:[.,](\d{1,2}))?$/.exec(importe.value);
      let comision = '-', neto = '-';
      if (partes) {
        const centavos = BigInt(partes[1]) * 100n + BigInt((partes[2] || '').padEnd(2, '0'));
        const descuento = (centavos + 5n) / 10n;
        const formato = n => (n / 100n).toString() + ',' + (n % 100n).toString().padStart(2, '0');
        comision = formato(descuento); neto = formato(centavos - descuento);
      }
      document.getElementById('comisionCalculada').textContent = comision;
      document.getElementById('netoCalculado').textContent = neto;
    };
    importe.addEventListener('input', mostrar); mostrar();
  }
  const inicio = document.getElementById('horaInicio'), fin = document.getElementById('horaFin');
  if (inicio && fin) {
    const validar = () => fin.setCustomValidity(inicio.value && fin.value && fin.value <= inicio.value
      ? 'La hora final debe ser posterior a la inicial del mismo dia.' : '');
    inicio.addEventListener('input', validar); fin.addEventListener('input', validar); validar();
  }
});
