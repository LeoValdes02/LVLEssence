package com.leo.tienda.perfumes.servicios;

import com.leo.tienda.perfumes.model.Perfume;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public interface ServicioPerfumes {

    void registrarPerfume(Perfume v);

    List<Perfume> obtenerPerfumes();

    Perfume obtenerPerfumesPorId(long id);

    void borrarPerfume(long id) throws Exception;

    void guardarCambiosPerfume(@Valid Perfume perfumeEditar) throws Exception;

    List<Map<String, Object>> obtenerJSONperfumes();

    Map<String,Object> obtenerJSONPerfumesPorId(Long id);
}