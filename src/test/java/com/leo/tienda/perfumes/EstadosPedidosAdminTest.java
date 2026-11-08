package com.leo.tienda.perfumes;

import com.leo.tienda.perfumes.constantesPedidos.EstadosPedido;
import com.leo.tienda.perfumes.model.Pedido;
import com.leo.tienda.perfumes.model.Usuario;
import com.leo.tienda.perfumes.servicios.ServicioPedidos;
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

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Fase 2: pruebas de estados administrativos con una H2 temporal, no la base de desarrollo.
 */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:estadospedidotest;DB_CLOSE_DELAY=-1")
class EstadosPedidosAdminTest {
    @Autowired ServicioPedidos pedidos;
    @Autowired EntityManager em;
    @Autowired TransactionTemplate tx;
    @Autowired WebApplicationContext context;
    @Autowired @Qualifier("springSecurityFilterChain") Filter security;

    private long crearPedido(EstadosPedido estado) {
        return tx.execute(s -> {
            Usuario usuario = new Usuario("estados-" + UUID.randomUUID() + "@example.test",
                    "clave", "Prueba", "Pedidos", "600000000");
            em.persist(usuario);
            Pedido pedido = new Pedido();
            pedido.setUsuario(usuario);
            pedido.setEstado(estado.name());
            em.persist(pedido);
            em.flush();
            return pedido.getId();
        });
    }

    @Test
    void transicionesDefinidasSinHabilitarEnProceso() {
        assertEquals(List.of(), EstadosPedido.destinosAdministracion("EnProceso"));
        assertEquals(List.of(EstadosPedido.Enviado, EstadosPedido.Incidencia),
                EstadosPedido.destinosAdministracion("Completado"));
        assertEquals(List.of(EstadosPedido.Incidencia),
                EstadosPedido.destinosAdministracion("Enviado"));
        assertEquals(List.of(EstadosPedido.Completado, EstadosPedido.Enviado),
                EstadosPedido.destinosAdministracion("Incidencia"));
    }

    @Test
    void servicioImpideSaltarseElCheckoutYValidaEstados() {
        long enProceso = crearPedido(EstadosPedido.EnProceso);
        assertThrows(IllegalStateException.class,
                () -> pedidos.actualizarEstadoPedido(enProceso, "Enviado"));
        assertThrows(IllegalStateException.class,
                () -> pedidos.actualizarEstadoPedido(enProceso, "Completado"));
        assertEquals("EnProceso", pedidos.obtenerPedidoPorId(enProceso).getEstado());

        long confirmado = crearPedido(EstadosPedido.Completado);
        assertThrows(IllegalArgumentException.class,
                () -> pedidos.actualizarEstadoPedido(confirmado, "Inexistente"));
        assertThrows(IllegalStateException.class,
                () -> pedidos.actualizarEstadoPedido(confirmado, "EnProceso"));
        assertEquals("Completado", pedidos.obtenerPedidoPorId(confirmado).getEstado());

        pedidos.actualizarEstadoPedido(confirmado, "Enviado");
        assertEquals("Enviado", pedidos.obtenerPedidoPorId(confirmado).getEstado());
        assertThrows(IllegalStateException.class,
                () -> pedidos.actualizarEstadoPedido(confirmado, "Completado"));
        pedidos.actualizarEstadoPedido(confirmado, "Incidencia");
        pedidos.actualizarEstadoPedido(confirmado, "Completado");
        assertEquals("Completado", pedidos.obtenerPedidoPorId(confirmado).getEstado());
    }

    @Test
    void endpointExigeAdminPostYCrsfYRenderizaDetalle() throws Exception {
        long id = crearPedido(EstadosPedido.Completado);
        var mvc = MockMvcBuilders.webAppContextSetup(context).addFilters(security).build();
        MockHttpSession session = new MockHttpSession();

        String inicio = mvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        var m = Pattern.compile("<meta name=\"csrf-token\" content=\"([^\"]+)\"")
                .matcher(inicio);
        assertTrue(m.find(), "La página inicial debe exponer el token CSRF");
        String token = m.group(1);

        mvc.perform(post("/admin/actualizarEstadoPedido").session(session)
                        .header("X-CSRF-TOKEN", token)
                        .param("id", String.valueOf(id)).param("estado", "Enviado"))
                .andExpect(redirectedUrl("/loginAdmin"));
        assertEquals("Completado", pedidos.obtenerPedidoPorId(id).getEstado());

        session.setAttribute("admin", "ok");
        mvc.perform(get("/admin/actualizarEstadoPedido").session(session))
                .andExpect(status().isMethodNotAllowed());
        mvc.perform(post("/admin/actualizarEstadoPedido").session(session)
                        .param("id", String.valueOf(id)).param("estado", "Enviado"))
                .andExpect(status().isForbidden());
        assertEquals("Completado", pedidos.obtenerPedidoPorId(id).getEstado());

        mvc.perform(get("/admin/verDetallesPedido").session(session)
                        .param("id", String.valueOf(id)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Gestionar estado del pedido")))
                .andExpect(content().string(containsString("Enviado")));

        mvc.perform(post("/admin/actualizarEstadoPedido").session(session)
                        .header("X-CSRF-TOKEN", token)
                        .param("id", String.valueOf(id)).param("estado", "Enviado"))
                .andExpect(redirectedUrl("/admin/verDetallesPedido?id=" + id))
                .andExpect(flash().attributeExists("mensaje"));
        assertEquals("Enviado", pedidos.obtenerPedidoPorId(id).getEstado());

        mvc.perform(post("/admin/actualizarEstadoPedido").session(session)
                        .header("X-CSRF-TOKEN", token)
                        .param("id", String.valueOf(id)).param("estado", "Completado"))
                .andExpect(redirectedUrl("/admin/verDetallesPedido?id=" + id))
                .andExpect(flash().attributeExists("error"));
        assertEquals("Enviado", pedidos.obtenerPedidoPorId(id).getEstado());
    }
}
