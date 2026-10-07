package com.leo.tienda.perfumes.constantesSQL;

public class ConstantesSQL {
    public final static String SQL_OBTENER_PERFUMES_JSON =
            "select tp.id, tp.nombre, tp.marca, tp.precio, c.nombre as categoria from TABLA_PERFUMES as tp left join CATEGORIA as c on tp.categoria_id = c.id";
}
