package com.leo.tienda.perfumes;

import com.leo.tienda.perfumes.model.*;
import jakarta.persistence.EntityManager;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:navegacion_premium;DB_CLOSE_DELAY=-1")
@Transactional
class NavegacionPremiumTest {
    @Autowired WebApplicationContext context;
    @Autowired @Qualifier("springSecurityFilterChain") Filter security;
    @Autowired EntityManager em;
    @Test void favoritosPrivadosPersistentesYCsrf() throws Exception {
        Usuario a = new Usuario("fase3a@example.test", "x", "Ana", "Prueba", "600000001");
        Usuario b = new Usuario("fase3b@example.test", "x", "Bea", "Prueba", "600000002");
        em.persist(a); em.persist(b);
        Perfumes p = new Perfumes("Favorito Prueba", "Marca", 100, "Floral", 100, "Parfum", "Unisex", "Prueba");
        p.setDescuentoPorcentaje(20); em.persist(p); em.flush();
        var mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(security).build();
        var sa = new MockHttpSession(); sa.setAttribute("usuario_identificado", a);
        var sb = new MockHttpSession(); sb.setAttribute("usuario_identificado", b);
        mvc.perform(post("/restFavoritos").session(sa).param("id", String.valueOf(p.getId()))).andExpect(status().isForbidden());
        String html = mvc.perform(get("/").session(sa)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var match = java.util.regex.Pattern.compile("<meta name=\"csrf-token\" content=\"([^\"]+)\"").matcher(html);
        assertTrue(match.find()); String token = match.group(1);
        mvc.perform(post("/restFavoritos").session(sa).header("X-CSRF-TOKEN", token).param("id", String.valueOf(p.getId()))).andExpect(status().isOk());
        mvc.perform(post("/restFavoritos").session(sa).header("X-CSRF-TOKEN", token).param("id", String.valueOf(p.getId()))).andExpect(status().isOk());
        mvc.perform(get("/restFavoritos").session(sa)).andExpect(status().isOk()).andExpect(jsonPath("$[0].precio").value(80)).andExpect(jsonPath("$[0].familiaOlfativa").value("Floral")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/restFavoritos").session(sb).param("idUsuario", String.valueOf(a.getId()))).andExpect(content().json("[]"));
        mvc.perform(get("/restFavoritos")).andExpect(status().isUnauthorized());
        mvc.perform(delete("/restFavoritos").session(sa).param("id", String.valueOf(p.getId()))).andExpect(status().isForbidden());
        mvc.perform(delete("/restFavoritos").session(sa).header("X-CSRF-TOKEN", token).param("id", String.valueOf(p.getId()))).andExpect(status().isOk());
        mvc.perform(get("/restFavoritos").session(sa)).andExpect(content().json("[]"));
    }
    @Test void menuYNotasUsanProductosReales() throws Exception {
        Perfumes p = new Perfumes("Ultimo Test", "Marca", 100, "Floral", 100, "Parfum", "Unisex", "Prueba");
        p.setNotasSalida("Bergamota, Limon"); p.setDescuentoPorcentaje(20); em.persist(p); em.flush();
        var mvc = MockMvcBuilders.webAppContextSetup(context).build();
        mvc.perform(get("/restPerfumes/menu")).andExpect(status().isOk())
            .andExpect(jsonPath("$.novedades[0].id").value(p.getId()))
            .andExpect(jsonPath("$.novedades[0].precio").value(80))
            .andExpect(jsonPath("$.novedades[0].precioOriginal").value(100));
        mvc.perform(get("/restPerfumes/notas")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("Bergamota")));
    }
}
