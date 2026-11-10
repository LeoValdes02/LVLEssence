package com.leo.tienda.perfumes;

import com.leo.tienda.perfumes.model.Categoria;
import com.leo.tienda.perfumes.model.Perfumes;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:rebranding1;DB_CLOSE_DELAY=-1")
@Transactional
class RebrandingFase1Test {
    @Autowired WebApplicationContext context;
    @Autowired EntityManager em;
    @Autowired @org.springframework.beans.factory.annotation.Qualifier("springSecurityFilterChain") jakarta.servlet.Filter security;
    MockHttpSession admin() { var s = new MockHttpSession(); s.setAttribute("admin", "ok"); return s; }
    MockMvc mvc() { return MockMvcBuilders.webAppContextSetup(context).build(); }
    MockHttpServletRequestBuilder campos(MockHttpServletRequestBuilder r, int categoria) {
        return r.session(admin()).param("nombre", "Perfume Prueba").param("marca", "Dior")
            .param("precio", "75").param("descuentoPorcentaje", "15").param("familiaOlfativa", "Floral")
            .param("mililitros", "100").param("concentracion", "Eau de Parfum")
            .param("genero", "Unisex").param("descripcion", "Descripcion de prueba")
            .param("idCategoria", String.valueOf(categoria)).param("stock", "12")
            .param("seleccionSalida", "Bergamota", "Limon", "Bergamota")
            .param("seleccionCorazon", "Rosa", "Jazmin").param("seleccionFondo", "Vainilla", "Cedro");
    }
    @Test void creaYActualizaSinPerderImagenNiDescuento() throws Exception {
        Categoria c = new Categoria("Test fase uno", "Test"); em.persist(c); em.flush();
        mvc().perform(campos(post("/admin/guardarPerfume"), c.getId()))
            .andExpect(status().isOk()).andExpect(view().name("admin/producto/registro_producto_ok"));
        em.flush(); em.clear();
        Perfumes p = em.createQuery("select p from Perfumes p where p.nombre = :n", Perfumes.class).setParameter("n", "Perfume Prueba").getSingleResult();
        assertEquals("Bergamota, Limon", p.getNotasSalida()); assertEquals("Rosa, Jazmin", p.getNotasCorazon());
        assertEquals(12, p.getStock()); assertEquals("Dior", p.getMarca()); assertEquals(c.getId(), p.getCategoria().getId());
        p.setImagenPortada(new byte[]{1, 2, 3}); em.flush(); long id = p.getId();
        mvc().perform(get("/admin/editarPerfume").session(admin()).param("id", String.valueOf(id)))
            .andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("Bergamota")));
        mvc().perform(campos(post("/admin/guardaCambiosrPerfume").param("stock", "22").param("seleccionSalida", "Lavanda"), c.getId()).param("id", String.valueOf(id)))
            .andExpect(status().is3xxRedirection());
        em.flush(); em.clear(); p = em.find(Perfumes.class, id);
        assertArrayEquals(new byte[]{1,2,3}, p.getImagenPortada()); assertEquals(15, p.getDescuentoPorcentaje());
        assertEquals(75, p.getPrecio()); assertEquals("Vainilla, Cedro", p.getNotasFondo());
        assertEquals(22, p.getStock()); assertEquals("Lavanda, Bergamota, Limon", p.getNotasSalida());
    }
    @Test void rechazaValoresInvalidosYRenderizaFormulario() throws Exception {
        Categoria c = new Categoria("Test invalidos", "Test"); em.persist(c); em.flush();
        mvc().perform(campos(post("/admin/guardarPerfume").param("stock", "-1").param("genero", "invalido").param("seleccionFondo", "desconocida"), c.getId()))
            .andExpect(status().isOk()).andExpect(view().name("admin/producto/registro_producto"))
            .andExpect(model().attributeHasFieldErrors("nuevo", "stock", "genero", "seleccionFondo"));
    }
    @Test void operacionesExigenSesionYCsrf() throws Exception {
        var mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(security).build();
        mvc.perform(post("/admin/guardarPerfume").session(admin())).andExpect(status().isForbidden());
        mvc.perform(post("/admin/guardaCambiosrPerfume").session(admin())).andExpect(status().isForbidden());
        mvc.perform(get("/admin/registrarPerfume")).andExpect(redirectedUrl("/loginAdmin"));
    }
    @Test void antiguoConNulosSeCarga() throws Exception {
        Categoria c = new Categoria("Test antiguo", "Test"); em.persist(c);
        Perfumes p = new Perfumes("Perfume Antiguo", "Marca anterior", 55, "Floral", 80, "Colonia antigua", "Antiguo", "Descripcion");
        p.setCategoria(c); em.persist(p); em.flush();
        mvc().perform(get("/admin/editarPerfume").session(admin()).param("id", String.valueOf(p.getId())))
            .andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("Marca anterior")));
        assertNull(p.getStock()); assertNull(p.getNotasSalida());
    }
    @Test void notasAntiguasYMarcaSeConservanAlEditar() throws Exception {
        Categoria c = new Categoria("Categoria antigua", "Test"); em.persist(c);
        Perfumes p = new Perfumes("Perfume Anterior", "Herm" + "?" + "s", 55, "Floral", 100, "Parfum", "Unisex", "Descripcion");
        p.setCategoria(c); p.setNotasSalida("Lim" + "?" + "n, Nota privada");
        p.setNotasCorazon("Jazm\u00edn"); p.setNotasFondo("S" + "?" + "ndalo");
        em.persist(p); em.flush(); long id = p.getId();
        mvc().perform(get("/admin/editarPerfume").session(admin()).param("id", String.valueOf(id)))
            .andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("Nota privada")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Limon")));
        mvc().perform(post("/admin/guardaCambiosrPerfume").session(admin()).param("id", String.valueOf(id))
            .param("nombre", "Perfume Anterior").param("marca", "Herm" + "?" + "s").param("precio", "55")
            .param("descuentoPorcentaje", "0").param("familiaOlfativa", "Floral").param("mililitros", "100")
            .param("concentracion", "Parfum").param("genero", "Unisex").param("descripcion", "Descripcion")
            .param("idCategoria", String.valueOf(c.getId())).param("seleccionSalida", "Limon", "Nota privada")
            .param("seleccionCorazon", "Jazmin").param("seleccionFondo", "Sandalo"))
            .andExpect(status().is3xxRedirection());
        em.flush(); em.clear(); p = em.find(Perfumes.class, id);
        assertEquals("Herm" + "?" + "s", p.getMarca());
        assertEquals("Limon, Nota privada", p.getNotasSalida());
        assertEquals("Jazmin", p.getNotasCorazon()); assertEquals("Sandalo", p.getNotasFondo());
    }

}
