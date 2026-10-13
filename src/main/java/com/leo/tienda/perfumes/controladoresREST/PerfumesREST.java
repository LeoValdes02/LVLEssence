package com.leo.tienda.perfumes.controladoresREST;

import com.leo.tienda.perfumes.servicios.ServicioPerfumes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

//Un servicio REST
//se le puede llamar tambien API
//esta archivo seria el API de perfumes

//@RestController no devuelven vistas/htmls sino json
@RestController
@RequestMapping("restPerfumes/")
public class PerfumesREST {

    @Autowired
    private ServicioPerfumes servicioPerfumes;

    // ahora que se ha complicado un poco
    // vamos a tomar más control en devolver el json con el
    // listado de videojuegos

    @RequestMapping("listar")
    public List<Map<String, Object>> listar() {
        return servicioPerfumes.obtenerJSONperfumes();
    }

    @RequestMapping("obtener_perfume")
    public Map<String, Object> obtenerPerfume(@RequestParam Long id) {
        //estos return de un @RestController devuelven json
        return servicioPerfumes.obtenerJSONPerfumesPorId(id);
    }

}
