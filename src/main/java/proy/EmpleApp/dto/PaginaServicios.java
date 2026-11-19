package proy.EmpleApp.dto;

import java.util.List;

/** Contrato de paginación estable e independiente de las clases internas de Spring. */
public record PaginaServicios(List<ServicioResumen> contenido, int pagina, int tamano,
        long totalElementos, int totalPaginas) { }
