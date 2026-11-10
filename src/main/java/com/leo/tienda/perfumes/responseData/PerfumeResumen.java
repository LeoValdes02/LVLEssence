package com.leo.tienda.perfumes.responseData;

/** Resumen de un perfume, sin datos binarios de imagen. */
public record PerfumeResumen(long id, String nombre, String marca,
                             double precio, double precioOriginal,
                             int descuentoPorcentaje, String categoria, String familiaOlfativa) {
    public PerfumeResumen(long id, String nombre, String marca, double precio, double precioOriginal,
                          int descuentoPorcentaje, String categoria) {
        this(id, nombre, marca, precio, precioOriginal, descuentoPorcentaje, categoria, null);
    }
    public boolean enOferta() {
        return descuentoPorcentaje > 0;
    }
}
