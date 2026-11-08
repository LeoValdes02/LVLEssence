package com.leo.tienda.perfumes;

import com.leo.tienda.perfumes.model.Categoria;
import com.leo.tienda.perfumes.model.Perfumes;
import com.leo.tienda.perfumes.responseData.PaginaPerfumes;
import com.leo.tienda.perfumes.servicios.ServicioPerfumes;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Pruebas sobre H2 en memoria; no toca data/base_de_datos.mv.db. */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:busqueda_perfumes;DB_CLOSE_DELAY=-1")
class BusquedaPerfumesTest {

    @Autowired ServicioPerfumes servicio;
    @Autowired EntityManager em;
    @Autowired WebApplicationContext context;

    @Test
    @Transactional
    void buscaPorNombreCategoriaPrecioYOrden() {
        Categoria categoria = em.createQuery("select c from Categoria c order by c.id", Categoria.class)
                .setMaxResults(1).getSingleResult();
        for (int i = 0; i < 12; i++) {
            Perfumes p = new Perfumes("QA Catalogo " + i, "Marca", 50 + i,
                    "Fresca", 100, "Eau de Parfum", "Unisex", "Producto de prueba");
            p.setCategoria(categoria);
            em.persist(p);
        }
        em.flush();

        PaginaPerfumes pagina1 = servicio.buscarPerfumes("QA Catalogo", categoria.getId(),
                null, null, "precioAsc", 1);
        PaginaPerfumes pagina2 = servicio.buscarPerfumes("qa catalogo", categoria.getId(),
                null, null, "precioAsc", 2);
        assertEquals(12, pagina1.total());
        assertEquals(2, pagina1.totalPaginas());
        assertEquals(10, pagina1.perfumes().size());
        assertEquals(2, pagina2.perfumes().size());
        assertTrue(pagina1.perfumes().get(0).precio() < pagina2.perfumes().get(1).precio());

        PaginaPerfumes precio = servicio.buscarPerfumes("QA Catalogo", categoria.getId(),
                52.0, 54.0, "precioDesc", 1);
        assertEquals(3, precio.total());
        assertEquals(54, precio.perfumes().get(0).precio());
        assertEquals(52, precio.perfumes().get(2).precio());
        assertTrue(servicio.buscarPerfumes("NO_EXISTE_QA", null, null, null, "nombreAsc", 1)
                .perfumes().isEmpty());
    }

    @Test
    void erroresDeParametrosDevuelven400() throws Exception {
        var mvc = MockMvcBuilders.webAppContextSetup(context).build();
        mvc.perform(get("/restPerfumes/buscar").param("pagina", "0"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/restPerfumes/buscar").param("precioMin", "100")
                        .param("precioMax", "50"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/restPerfumes/buscar").param("orden", "DROP TABLE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void mantieneListadoAntiguoYExponeNuevoFormato() throws Exception {
        var mvc = MockMvcBuilders.webAppContextSetup(context).build();
        mvc.perform(get("/restPerfumes/listar"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].ID").exists());
        mvc.perform(get("/restPerfumes/buscar").param("texto", "Sauvage"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.perfumes[0].nombre").value("Sauvage"))
                .andExpect(jsonPath("$.pagina").value(1))
                .andExpect(jsonPath("$.tamano").value(10));
    }
}
