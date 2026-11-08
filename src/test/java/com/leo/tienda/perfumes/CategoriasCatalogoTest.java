package com.leo.tienda.perfumes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Test aislado: no abre la base H2 de desarrollo. */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:categorias_catalogo;DB_CLOSE_DELAY=-1")
class CategoriasCatalogoTest {

    @Autowired WebApplicationContext context;

    @Test
    void categoriasDevuelveIdsYNombres() throws Exception {
        var mvc = MockMvcBuilders.webAppContextSetup(context).build();
        mvc.perform(get("/restPerfumes/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].nombre").isString());
    }
}
