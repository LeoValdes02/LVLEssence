package com.leo.tienda.perfumes.controladoresREST;

import com.leo.tienda.perfumes.servicios.ServicioPerfumes;
import com.leo.tienda.perfumes.servicios.ServicioCategorias;
import com.leo.tienda.perfumes.responseData.CategoriaFiltro;
import com.leo.tienda.perfumes.responseData.PaginaPerfumes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Comparator;
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

    @Autowired
    private ServicioCategorias servicioCategorias;

    // Devuelve los IDs reales de cada categoría sin exponer entidades JPA.
    @GetMapping("categorias")
    public List<CategoriaFiltro> listarCategorias() {
        return servicioCategorias.obtenerCategorias().stream()
                .map(c -> new CategoriaFiltro(c.getId(), c.getNombre()))
                .sorted(Comparator.comparing(CategoriaFiltro::nombre, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    // ahora que se ha complicado un poco
    // vamos a tomar más control en devolver el json con el
    // listado de videojuegos

    @RequestMapping("listar")
    public List<Map<String, Object>> listar() {
        return servicioPerfumes.obtenerJSONperfumes();
    }

    /**
     * Ruta nueva. Mantiene intacta /restPerfumes/listar para el JS actual.
     * Ej.: /restPerfumes/buscar?texto=sauvage&pagina=1&orden=precioAsc
     */
    @GetMapping("buscar")
    public PaginaPerfumes buscar(
            @RequestParam(defaultValue = "") String texto,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(defaultValue = "nombreAsc") String orden,
            @RequestParam(defaultValue = "1") int pagina) {
        try {
            return servicioPerfumes.buscarPerfumes(
                    texto, categoriaId, precioMin, precioMax, orden, pagina);
        } catch (IllegalArgumentException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error.getMessage());
        }
    }

    @RequestMapping("obtener_perfume")
    public Map<String, Object> obtenerPerfume(@RequestParam Long id) {
        //estos return de un @RestController devuelven json
        return servicioPerfumes.obtenerJSONPerfumesPorId(id);
    }

}
