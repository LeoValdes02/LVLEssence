package com.leo.tienda.perfumes.responseData;

import java.util.List;

/** Resultado de una búsqueda: elementos de la página y metadatos de paginación. */
public record PaginaPerfumes(List<PerfumeResumen> perfumes, long total,
                             int pagina, int tamano, long totalPaginas) {
}
