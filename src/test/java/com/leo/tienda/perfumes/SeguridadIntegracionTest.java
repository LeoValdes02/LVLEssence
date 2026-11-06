package com.leo.tienda.perfumes;

import com.leo.tienda.perfumes.model.Usuario;
import com.leo.tienda.perfumes.servicios.ServicioUsuarios;
import com.leo.tienda.perfumes.servicios.ServicioPerfumes;
import com.leo.tienda.perfumes.servicios.ServicioCarrito;
import jakarta.persistence.EntityManager;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:seguridadtest;DB_CLOSE_DELAY=-1")
class SeguridadIntegracionTest {
    @Autowired WebApplicationContext context;
    @Autowired @Qualifier("springSecurityFilterChain") Filter security;
    @Autowired ServicioUsuarios usuarios;
    @Autowired ServicioPerfumes perfumes;
    @Autowired ServicioCarrito carrito;
    @Autowired EntityManager em;
    @Autowired TransactionTemplate tx;

    @Test void csrfSesionYAdministracion() throws Exception {
        var mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(security).build();
        var session = new MockHttpSession();
        String pagina = mvc.perform(get("/").session(session)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var match = java.util.regex.Pattern.compile("<meta name=\"csrf-token\" content=\"([^\"]+)\"").matcher(pagina);
        assertTrue(match.find(), "El inicio debe publicar un token CSRF para AJAX");
        String token = match.group(1);
        mvc.perform(post("/loginAdmin").session(session).param("pass-login-admin", "123")).andExpect(status().isForbidden());
        mvc.perform(post("/loginAdmin").session(session).header("X-CSRF-TOKEN", "invalido").param("pass-login-admin", "123")).andExpect(status().isForbidden());
        String id = session.getId();
        mvc.perform(post("/loginAdmin").session(session).header("X-CSRF-TOKEN", token).param("pass-login-admin", "123")).andExpect(redirectedUrl("/admin/gestionarPerfumes"));
        assertNotEquals(id, session.getId());
        mvc.perform(get("/admin/gestionarPerfumes").session(session)).andExpect(status().isOk());
        mvc.perform(get("/admin/borrarPerfume").session(session).param("id", "1")).andExpect(status().isMethodNotAllowed());
        mvc.perform(post("/admin/cerrarSesion").session(session).header("X-CSRF-TOKEN", token)).andExpect(status().is3xxRedirection());
        mvc.perform(get("/admin/gestionarPedidos").session(session)).andExpect(redirectedUrl("/loginAdmin"));
        for (String ruta : new String[] {"/admin", "/admin/inicioAdmin", "/admin/gestionarPerfumes", "/admin/registrarPerfume", "/admin/editarPerfume", "/admin/gestionarCategorias", "/admin/registrarCategoria", "/admin/editarCategoria", "/admin/gestionarUsuarios", "/admin/registrarUsuario", "/admin/editarUsuario", "/admin/gestionarPedidos", "/admin/verDetallesPedido"}) {
            mvc.perform(get(ruta).session(session)).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/loginAdmin"));
        }
        usuarios.registrarUsuario(new Usuario("sesion@example.test", "prueba", "Cliente", "Test", "600000000"));
        mvc.perform(post("/restUsuarios/identificarUsuario").session(session).header("X-CSRF-TOKEN", token).param("email", "sesion@example.test").param("pass", "error")).andExpect(status().isUnauthorized());
        id = session.getId();
        mvc.perform(post("/restUsuarios/identificarUsuario").session(session).header("X-CSRF-TOKEN", token).param("email", "sesion@example.test").param("pass", "prueba")).andExpect(status().isOk());
        assertNotEquals(id, session.getId());
        String json = mvc.perform(get("/restUsuarios/sesion").session(session)).andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Cliente")).andReturn().getResponse().getContentAsString();
        assertFalse(json.contains("pass"));
        mvc.perform(get("/restUsuarios/identificarUsuario").param("email", "x").param("pass", "x")).andExpect(status().isMethodNotAllowed());
        mvc.perform(post("/restUsuarios/cerrarSesion").session(session).header("X-CSRF-TOKEN", token)).andExpect(status().isOk());
        mvc.perform(get("/restCarrito/obtenerProductos").session(session)).andExpect(status().isUnauthorized());
    }

    @Test void migracionYProteccionHistorial() throws Exception {
        Usuario antiguo = new Usuario("antiguo@example.test", "claveAnterior", "Cliente", "Antiguo", "600000000");
        tx.executeWithoutResult(s -> em.persist(antiguo));
        assertNull(usuarios.obtenerUsuarioPorEmailYpass("antiguo@example.test", "incorrecta"));
        assertEquals("claveAnterior", usuarios.obtenerUsuarioPorId(antiguo.getId()).getPass());
        assertNotNull(usuarios.obtenerUsuarioPorEmailYpass("antiguo@example.test", "claveAnterior"));
        Usuario migrado = usuarios.obtenerUsuarioPorId(antiguo.getId());
        assertTrue(migrado.getPassCodificada());
        assertNotEquals("claveAnterior", migrado.getPass());
        assertNotNull(usuarios.obtenerUsuarioPorEmailYpass("antiguo@example.test", "claveAnterior"));
        long id = em.createQuery("select p.perfume.id from ProductoPedido p", Long.class).setMaxResults(1).getSingleResult();
        assertThrows(IllegalStateException.class, () -> perfumes.borrarPerfume(id));
    }

    @Test void hashesYCarritosAislados() throws Exception {
        Usuario a = new Usuario("uno@example.test", "igual", "Uno", "Test", "600000000");
        Usuario b = new Usuario("dos@example.test", "igual", "Dos", "Test", "600000000");
        usuarios.registrarUsuario(a); usuarios.registrarUsuario(b);
        assertNotEquals(a.getPass(), b.getPass());
        String hash = a.getPass();
        a.setPass(""); usuarios.guardarCambiosUsuario(a);
        assertEquals(hash, usuarios.obtenerUsuarioPorId(a.getId()).getPass());
        long producto = em.createQuery("select p.id from Perfumes p", Long.class).setMaxResults(1).getSingleResult();
        carrito.agregarProducto(a.getId(), producto, 2);
        var sesionB = new MockHttpSession(); sesionB.setAttribute("usuario_identificado", b);
        var mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(security).build();
        mvc.perform(get("/restCarrito/obtenerProductos").session(sesionB).param("idUsuario", String.valueOf(a.getId())))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        assertEquals(2, carrito.obtenerProductosCarrito(a.getId()).get(0).get("cantidad"));
    }
}
