package proy.EmpleApp;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import proy.EmpleApp.modelo.*;
import java.math.BigDecimal;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;

class ReglasNegocioTests {
    @Test void rechazaHorarioInvertidoOIncompleto() {
        Servicio s = new Servicio();
        s.setFecha(LocalDate.of(2026, 12, 1));
        s.setHoraInicio(LocalTime.of(14, 0));
        s.setHoraFin(LocalTime.of(10, 0));
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            assertTrue(factory.getValidator().validate(s).stream().anyMatch(v -> v.getPropertyPath().toString().equals("horarioValido")));
        }
        s.setHoraFin(LocalTime.of(14, 0)); assertFalse(s.isHorarioValido());
        s.setHoraFin(null); assertFalse(s.isHorarioValido());
        s.setHoraFin(LocalTime.of(15, 0)); assertTrue(s.isHorarioValido());
    }
    @Test void calculaComisionIgnorandoValoresManipulados() {
        Pago p = new Pago(); p.setValorServicio(new BigDecimal("100000"));
        p.setPorcentajeComisionApp(new BigDecimal("99"));
        p.setValorComision(BigDecimal.ZERO); p.setValorTrabajador(BigDecimal.ZERO);
        p.calcularImportes();
        assertEquals(new BigDecimal("10.00"), p.getPorcentajeComisionApp());
        assertEquals(new BigDecimal("10000.00"), p.getValorComision());
        assertEquals(new BigDecimal("90000.00"), p.getValorTrabajador());
    }
    @Test void redondeaComisionYConservaTotal() {
        Pago p = new Pago(); p.setValorServicio(new BigDecimal("100.05")); p.calcularImportes();
        assertEquals(new BigDecimal("10.01"), p.getValorComision());
        assertEquals(p.getValorServicio(), p.getValorComision().add(p.getValorTrabajador()));
    }
    @Test void rechazaImportesInvalidos() {
        Pago p = new Pago();
        assertThrows(IllegalArgumentException.class, p::calcularImportes);
        for (String valor : new String[]{"0", "-1", "100.123", "100000000"}) {
            p.setValorServicio(new BigDecimal(valor));
            assertThrows(IllegalArgumentException.class, p::calcularImportes);
        }
    }
    @Test void etiquetaTrabajadorContieneNombre() {
        Usuario u = new Usuario(); u.setNombre("Maria Munoz");
        Trabajador t = new Trabajador(); t.setIdTrabajador(7); t.setUsuario(u);
        assertEquals("7 - Maria Munoz", t.getEtiqueta());
    }
}
