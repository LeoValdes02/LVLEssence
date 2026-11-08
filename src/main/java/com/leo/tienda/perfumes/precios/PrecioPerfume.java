package com.leo.tienda.perfumes.precios;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Fuente única del cálculo de un precio de venta (redondeo a céntimos). */
public final class PrecioPerfume {

    private PrecioPerfume() { }

    public static BigDecimal calcular(double precioOriginal, int porcentajeDescuento) {
        if (!Double.isFinite(precioOriginal) || precioOriginal < 0) {
            throw new IllegalArgumentException("El precio original no es válido");
        }
        if (porcentajeDescuento < 0 || porcentajeDescuento > 100) {
            throw new IllegalArgumentException("El descuento debe estar entre 0 y 100 %");
        }
        return BigDecimal.valueOf(precioOriginal)
                .multiply(BigDecimal.valueOf(100L - porcentajeDescuento))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
