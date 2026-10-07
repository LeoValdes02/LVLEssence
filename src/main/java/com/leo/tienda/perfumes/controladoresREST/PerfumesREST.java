package com.leo.tienda.perfumes.controladoresREST;

import com.leo.tienda.perfumes.model.Perfume;
import com.leo.tienda.perfumes.servicios.ServicioPerfumes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("restPerfumes/")
public class PerfumesREST {

    @Autowired
    private ServicioPerfumes servicioPerfumes;

    @RequestMapping("listar")
    public List<Map<String, Object>> listar() {
        return servicioPerfumes.obtenerJSONperfumes();
    }

    @RequestMapping("obtener_perfume")
    public Perfume obtenerPerfume(@RequestParam Long id) {
        return servicioPerfumes.obtenerPerfumesPorId(id);
    }

}
