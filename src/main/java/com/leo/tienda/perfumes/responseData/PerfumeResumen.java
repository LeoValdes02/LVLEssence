package com.leo.tienda.perfumes.responseData;

/** Resumen de un perfume, sin datos binarios de imagen. */
public record PerfumeResumen(long id, String nombre, String marca,
                             double precio, double precioOriginal,
                             int descuentoPorcentaje, String categoria) {
    public boolean enOferta() {
        return descuentoPorcentaje > 0;
    }
}
