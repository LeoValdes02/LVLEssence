package com.leo.tienda.perfumes.servicios;

import com.leo.tienda.perfumes.model.Perfumes;
import com.leo.tienda.perfumes.responseData.PaginaPerfumes;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public interface ServicioPerfumes {

    void registrarPerfume(Perfumes v); //

    List<Perfumes> obtenerPerfumes();

    /**
     * Búsqueda combinada, 10 perfumes por página (numeración desde 1).
     * Los filtros y la ordenación usan el precio final con descuento.
     */
    PaginaPerfumes buscarPerfumes(String texto, Integer categoriaId,
                                  Double precioMin, Double precioMax,
                                  String orden, int pagina);

    /** Carga en una sola consulta los perfumes de la página administrativa. */
    List<Perfumes> obtenerPerfumesPorIds(List<Long> ids);

    /** Aplica un porcentaje (0-100) solo a los perfumes elegidos. */
    void aplicarDescuentoEnLote(List<Long> ids, int porcentaje);

    Perfumes obtenerPerfumesPorId(long id);

    void borrarPerfume(long id) throws Exception;

    void guardarCambiosPerfume(@Valid Perfumes perfumeEditar) throws Exception;

    List<Map<String, Object>> obtenerJSONperfumes();

    Map<String,Object> obtenerJSONPerfumesPorId(Long id);
}