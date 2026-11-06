package com.leo.tienda.perfumes;

import com.leo.tienda.perfumes.servicios.ServicioPedidos;
import com.leo.tienda.perfumes.servicios.ServicioSetUp;
import com.leo.tienda.perfumes.servicios.ServicioUsuarios;
import com.leo.tienda.perfumes.servicios.ServicioCarrito;
import com.leo.tienda.perfumes.model.Usuario;
import org.springframework.mock.web.MockHttpSession;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.support.TransactionTemplate;
import com.leo.tienda.perfumes.model.ProductoPedido;
import com.leo.tienda.perfumes.model.Perfumes;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:auditoriafinal;DB_CLOSE_DELAY=-1")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuditoriaFinalTest {
    @Autowired ServicioPedidos pedidos;
    @Autowired ServicioSetUp setup;
    @Autowired EntityManager em;
    @Autowired WebApplicationContext context;
    @Autowired ServicioUsuarios usuarios;
    @Autowired ServicioCarrito carrito;
    @Autowired TransactionTemplate tx;

    @Test void validacionesServidor() {
        assertThrows(IllegalArgumentException.class, () -> pedidos.procesarPaso1("", "direccion", "provincia", "ciudad", "28001", "", 1));
        assertThrows(IllegalArgumentException.class, () -> pedidos.procesarPaso1("nombre", "", "provincia", "ciudad", "28001", "", 1));
        assertThrows(IllegalArgumentException.class, () -> pedidos.procesarPaso1("nombre", "direccion", "provincia", "", "28001", "", 1));
        assertThrows(IllegalArgumentException.class, () -> pedidos.procesarPaso1("nombre", "direccion", "provincia", "ciudad", "123", "", 1));
        assertThrows(IllegalArgumentException.class, () -> pedidos.procesarPaso2("titular", "visa_demo", "prueba@gmail", 1));
        assertThrows(IllegalArgumentException.class, () -> pedidos.procesarPaso2("", "visa_demo", "prueba@example.com", 1));
    }

    @Test @Order(1) void inicioYRepeticion() {
        long categorias = em.createQuery("select count(c) from Categoria c", Long.class).getSingleResult();
        long perfumes = em.createQuery("select count(p) from Perfumes p", Long.class).getSingleResult();
        long pedidosAntes = em.createQuery("select count(p) from Pedido p", Long.class).getSingleResult();
        assertTrue(categorias >= 4); assertTrue(perfumes >= 10); assertEquals(1, pedidosAntes);
        assertEquals(1L, em.createQuery("select count(p) from Pedido p where p.estado = 'Completado' and p.ciudad is not null and p.codigoPostal is not null and p.instruccionesEntrega is not null and p.emailFacturacion is not null and p.envoltorioRegalo is not null", Long.class).getSingleResult());
        assertEquals(2L, em.createQuery("select count(p) from ProductoPedido p where p.cantidad > 0", Long.class).getSingleResult());
        setup.prepararRegistrosIniciales(); setup.prepararPedidoEjemplo();
        assertEquals(pedidosAntes, em.createQuery("select count(p) from Pedido p", Long.class).getSingleResult());
    }

    @Test @Order(2) void regresionYCheckoutHttp() throws Exception {
        Usuario u = new Usuario("qa@example.test", "ficticia", "QA", "Test", "600000000");
        usuarios.registrarUsuario(u);
        assertThrows(IllegalArgumentException.class, () -> usuarios.registrarUsuario(new Usuario(" QA@EXAMPLE.TEST ", "x", "x", "x", "x")));
        assertNotNull(usuarios.obtenerUsuarioPorEmailYpass("qa@example.test", "ficticia"));
        assertNull(usuarios.obtenerUsuarioPorEmailYpass("qa@example.test", "incorrecta"));
        long perfume = em.createQuery("select p.id from Perfumes p order by p.id", Long.class).setMaxResults(1).getSingleResult();
        carrito.agregarProducto(u.getId(), perfume, 2);
        carrito.agregarProducto(u.getId(), perfume, 1);
        assertEquals(3, carrito.obtenerProductosCarrito(u.getId()).get(0).get("cantidad"));
        carrito.cambiarCantidad(u.getId(), perfume, 4);
        assertEquals(4, carrito.obtenerProductosCarrito(u.getId()).get(0).get("cantidad"));
        carrito.borrarProducto(u.getId(), perfume);
        assertTrue(carrito.obtenerProductosCarrito(u.getId()).isEmpty());
        var mvc = MockMvcBuilders.webAppContextSetup(context).build();
        var session = new MockHttpSession(); session.setAttribute("usuario_identificado", u);
        mvc.perform(post("/restPedido/procesarPaso4").session(session)).andExpect(status().isConflict());
        mvc.perform(post("/restPedido/procesarPaso1").session(session).param("nombre", "QA").param("direccion", "calle").param("provincia", "Madrid").param("ciudad", "Madrid").param("codigoPostal", "28001")).andExpect(status().isOk());
        mvc.perform(post("/restPedido/procesarPaso2").session(session).param("tarjeta", "visa_demo").param("titular", "QA").param("emailFacturacion", "prueba@example.com")).andExpect(status().isOk());
        mvc.perform(post("/restPedido/procesarPaso3").session(session)).andExpect(status().isBadRequest());
        mvc.perform(post("/restPedido/procesarPaso3").session(session).param("envoltorioRegalo", "no")).andExpect(status().isOk()).andExpect(jsonPath("$.emailFacturacion").value("prueba@example.com"));
        mvc.perform(post("/restPedido/procesarPaso4").session(session)).andExpect(status().isConflict());
        carrito.agregarProducto(u.getId(), perfume, 1);
        mvc.perform(post("/restPedido/procesarPaso4").session(session)).andExpect(status().isOk());
        assertTrue(carrito.obtenerProductosCarrito(u.getId()).isEmpty());
        assertEquals(1L, em.createQuery("select count(p) from Pedido p where p.usuario.id = :id and p.estado = 'Completado'", Long.class).setParameter("id", u.getId()).getSingleResult());
        ProductoPedido linea = em.createQuery("select p from ProductoPedido p where p.pedido.usuario.id = :id", ProductoPedido.class).setParameter("id", u.getId()).getSingleResult();
        var precioHistorico = linea.getPrecioUnitario();
        assertNotNull(precioHistorico);
        tx.executeWithoutResult(s -> em.find(Perfumes.class, perfume).setPrecio(999));
        ProductoPedido lineaRecargada = em.find(ProductoPedido.class, linea.getId());
        assertEquals(0, precioHistorico.compareTo(lineaRecargada.getPrecioAplicable()));
        assertThrows(IllegalStateException.class, () -> usuarios.borrarUsuario(u.getId()));
        Usuario sinPedido = new Usuario("qa2@example.test", "ficticia", "QA", "Test", "600000000");
        usuarios.registrarUsuario(sinPedido); carrito.agregarProducto(sinPedido.getId(), perfume, 1);
        usuarios.borrarUsuario(sinPedido.getId());
        assertNull(usuarios.obtenerUsuarioPorId(sinPedido.getId()));
        assertEquals(0L, em.createQuery("select count(c) from Carrito c where c.usuario.id = :id", Long.class).setParameter("id", sinPedido.getId()).getSingleResult());
    }

    @Test void accesoSinSesion() throws Exception {
        var mvc = MockMvcBuilders.webAppContextSetup(context).build();
        mvc.perform(get("/admin/gestionarPerfumes")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/loginAdmin"));
        mvc.perform(get("/admin/gestionarPedidos")).andExpect(status().is3xxRedirection());
        mvc.perform(get("/admin/gestionarPerfumes").param("pass-login-admin", "123")).andExpect(status().is3xxRedirection());
        mvc.perform(post("/loginAdmin").param("pass-login-admin", "incorrecta")).andExpect(redirectedUrl("/loginAdmin?error=1"));
        mvc.perform(get("/restCarrito/obtenerProductos")).andExpect(status().isUnauthorized());
        mvc.perform(post("/restCarrito/borrarProducto").param("id", "1")).andExpect(status().isUnauthorized());
        mvc.perform(post("/restPedido/procesarPaso4")).andExpect(status().isUnauthorized());
    }
}
