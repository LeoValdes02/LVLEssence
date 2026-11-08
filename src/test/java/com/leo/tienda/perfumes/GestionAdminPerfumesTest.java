package com.leo.tienda.perfumes;

import com.leo.tienda.perfumes.model.Categoria;
import com.leo.tienda.perfumes.model.Perfumes;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Fase 1.3: pruebas de Thymeleaf/MockMvc usando H2 EN MEMORIA.
 * No conecta con data/base_de_datos.mv.db.
 */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:admin_fase_1_3;DB_CLOSE_DELAY=-1")
class GestionAdminPerfumesTest {

    @Autowired WebApplicationContext context;
    @Autowired EntityManager em;

    private MockHttpSession admin() {
        MockHttpSession sesion = new MockHttpSession();
        sesion.setAttribute("admin", "ok");
        return sesion;
    }

    @Test
    @Transactional
    void busquedaPaginadaAdminReutilizaServicioYRenderizaTarjetas() throws Exception {
        Categoria categoria = new Categoria("QA admin filtros", "Categoría de prueba");
        em.persist(categoria);
        for (int i = 0; i < 12; i++) {
            Perfumes perfume = new Perfumes(
                    "QA Admin " + i, "Marca QA", 200 + i,
                    "Fresca", 100, "Eau de Parfum", "Unisex", "Test");
            perfume.setCategoria(categoria);
            em.persist(perfume);
        }
        em.flush();

        var mvc = MockMvcBuilders.webAppContextSetup(context).build();
        var sesion = admin();

        mvc.perform(get("/admin/gestionarPerfumes").session(sesion)
                        .param("texto", "QA Admin")
                        .param("categoriaId", String.valueOf(categoria.getId()))
                        .param("orden", "precioAsc"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/producto/gestionar_productos"))
                .andExpect(model().attribute("total", 12L))
                .andExpect(model().attribute("perfumes", hasSize(10)))
                .andExpect(model().attribute("totalPaginas", 2L))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("QA Admin 0")));

        var segunda = mvc.perform(get("/admin/gestionarPerfumes").session(sesion)
                        .param("texto", "QA Admin")
                        .param("categoriaId", String.valueOf(categoria.getId()))
                        .param("orden", "precioAsc")
                        .param("pagina", "2"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("perfumes", hasSize(2)))
                .andReturn();

        @SuppressWarnings("unchecked")
        var perfumes = (java.util.List<Perfumes>) segunda.getModelAndView()
                .getModel().get("perfumes");
        assertEquals("QA Admin 10", perfumes.get(0).getNombre());
        assertEquals("QA Admin 11", perfumes.get(1).getNombre());

        mvc.perform(get("/admin/gestionarPerfumes").session(sesion)
                        .param("texto", "QA Admin")
                        .param("categoriaId", String.valueOf(categoria.getId()))
                        .param("precioMin", "205")
                        .param("precioMax", "207"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("total", 3L))
                .andExpect(model().attribute("perfumes", hasSize(3)));
    }

    @Test
    void mantieneSeguridadYGestionaErroresDeFiltros() throws Exception {
        var mvc = MockMvcBuilders.webAppContextSetup(context).build();

        mvc.perform(get("/admin/gestionarPerfumes"))
                .andExpect(redirectedUrl("/loginAdmin"));

        mvc.perform(get("/admin/borrarPerfume").session(admin()).param("id", "1"))
                .andExpect(status().isMethodNotAllowed());

        mvc.perform(get("/admin/gestionarPerfumes").session(admin())
                        .param("precioMin", "200").param("precioMax", "50"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"));

        // Borrado por POST redirige y mantiene los parámetros; la protección
        // CSRF real ya está cubierta en SeguridadIntegracionTest.
        mvc.perform(post("/admin/borrarPerfume").session(admin())
                        .param("id", "999999999")
                        .param("texto", "Sauvage")
                        .param("orden", "precioDesc")
                        .param("pagina", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/gestionarPerfumes*"))
                .andExpect(flash().attributeExists("error"));
    }
}
