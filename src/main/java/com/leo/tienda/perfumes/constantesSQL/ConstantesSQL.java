package com.leo.tienda.perfumes.constantesSQL;

public class ConstantesSQL {
    // Mantener PRECIO como precio final para que las plantillas existentes sigan funcionando.
    // La expresión ROUND reproduce el redondeo a dos decimales de PrecioPerfume.
    private static final String CAMPOS_COMUNES =
            "tp.id, tp.nombre, tp.marca, " +
            "ROUND(tp.precio * (100 - COALESCE(tp.descuento_porcentaje, 0)) / 100.0, 2) AS PRECIO, " +
            "tp.precio AS PRECIO_ORIGINAL, " +
            "COALESCE(tp.descuento_porcentaje, 0) AS DESCUENTO_PORCENTAJE, " +
            "CASE WHEN COALESCE(tp.descuento_porcentaje, 0) > 0 THEN TRUE ELSE FALSE END AS TIENE_DESCUENTO, " +
            "c.nombre AS CATEGORIA ";

    public static final String SQL_OBTENER_PERFUMES_JSON =
            "SELECT " + CAMPOS_COMUNES +
            "FROM TABLA_PERFUMES tp LEFT JOIN CATEGORIA c ON tp.categoria_id = c.id";

    public static final String SQL_OBTENER_PERFUME_JSON =
            "SELECT " + CAMPOS_COMUNES + ", tp.descripcion AS DESCRIPCION " +
            "FROM TABLA_PERFUMES tp LEFT JOIN CATEGORIA c ON tp.categoria_id = c.id " +
            "WHERE tp.id = :id";
}
