package com.leo.tienda.perfumes;

import com.leo.tienda.perfumes.model.Categoria;
import com.leo.tienda.perfumes.model.Perfumes;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:detalles_premium;DB_CLOSE_DELAY=-1")
@Transactional
class DetallesPremiumTest {
    @Autowired WebApplicationContext context;
    @Autowired EntityManager em;

    @Test void devuelveDatosRealesYPreciosConDescuento() throws Exception {
        Categoria categoria = new Categoria("Prueba detalles", "test");
        em.persist(categoria);
        Perfumes p = new Perfumes("Nombre Real", "Marca Real", 100, "Floral", 75, "Eau de Parfum", "Unisex", "Descripcion real");
        p.setCategoria(categoria); p.setDescuentoPorcentaje(20);
        p.setNotasSalida("Bergamota, Limon"); p.setNotasCorazon("Rosa"); p.setNotasFondo("Cedro");
        p.setImagenPortada(new byte[]{1, 2, 3}); em.persist(p); em.flush();
        var mvc = MockMvcBuilders.webAppContextSetup(context).build();
        mvc.perform(get("/restPerfumes/obtener_perfume").param("id", String.valueOf(p.getId())))
            .andExpect(status().isOk()).andExpect(jsonPath("$.NOMBRE").value("Nombre Real"))
            .andExpect(jsonPath("$.MARCA").value("Marca Real")).andExpect(jsonPath("$.DESCRIPCION").value("Descripcion real"))
            .andExpect(jsonPath("$.CONCENTRACION").value("Eau de Parfum")).andExpect(jsonPath("$.MILILITROS").value(75))
            .andExpect(jsonPath("$.NOTAS_SALIDA").value("Bergamota, Limon"))
            .andExpect(jsonPath("$.NOTAS_CORAZON").value("Rosa")).andExpect(jsonPath("$.NOTAS_FONDO").value("Cedro"))
            .andExpect(jsonPath("$.PRECIO").value(80)).andExpect(jsonPath("$.PRECIO_ORIGINAL").value(100))
            .andExpect(jsonPath("$.DESCUENTO_PORCENTAJE").value(20)).andExpect(jsonPath("$.TIENE_DESCUENTO").value(true))
            .andExpect(jsonPath("$.imagenPortada").doesNotExist());
        mvc.perform(get("/mostrar_imagen").param("id", String.valueOf(p.getId())))
            .andExpect(status().isOk()).andExpect(content().bytes(new byte[]{1, 2, 3}));
    }
    @Test void admiteNotasNulasYResponde404ParaIdInexistente() throws Exception {
        Perfumes p = new Perfumes("Antiguo", "Marca", 25, "Floral", 100, "Parfum", "Unisex", "Sin notas");
        em.persist(p); em.flush();
        var mvc = MockMvcBuilders.webAppContextSetup(context).build();
        mvc.perform(get("/restPerfumes/obtener_perfume").param("id", String.valueOf(p.getId())))
            .andExpect(status().isOk()).andExpect(jsonPath("$.PRECIO").value(25))
            .andExpect(jsonPath("$.TIENE_DESCUENTO").value(false)).andExpect(jsonPath("$.NOTAS_SALIDA").doesNotExist());
        mvc.perform(get("/restPerfumes/obtener_perfume").param("id", "999999999"))
            .andExpect(status().isNotFound());
    }
}
