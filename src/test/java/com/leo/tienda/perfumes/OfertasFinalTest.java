package com.leo.tienda.perfumes;

import com.leo.tienda.perfumes.model.Perfumes;
import com.leo.tienda.perfumes.model.ProductoPedido;
import com.leo.tienda.perfumes.model.Usuario;
import com.leo.tienda.perfumes.precios.PrecioPerfume;
import com.leo.tienda.perfumes.responseData.PaginaPerfumes;
import com.leo.tienda.perfumes.servicios.ServicioCarrito;
import com.leo.tienda.perfumes.servicios.ServicioPedidos;
import com.leo.tienda.perfumes.servicios.ServicioPerfumes;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Regresión de ofertas de la fase 3. La URL explícita de H2 en memoria evita
 * tocar data/base_de_datos.mv.db durante estas pruebas.
 *
 * Estos tests están preparados, pero NO se han ejecutado en el entorno que creó el paquete.
 */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:ofertas_final;DB_CLOSE_DELAY=-1")
class OfertasFinalTest {

    @Autowired EntityManager em;
    @Autowired TransactionTemplate tx;
    @Autowired ServicioPerfumes perfumes;
    @Autowired ServicioCarrito carrito;
    @Autowired ServicioPedidos pedidos;
    @Autowired WebApplicationContext webContext;
    @Autowired @Qualifier("springSecurityFilterChain") Filter filtroSeguridad;

    private long crearPerfume(String nombre, double precio, int descuento) {
        return tx.execute(status -> {
            Perfumes perfume = new Perfumes(nombre, "Marca Test", precio,
                    "Fresca", 100, "Eau de Parfum", "Unisex", "Producto de auditoría");
            perfume.setDescuentoPorcentaje(descuento);
            em.persist(perfume);
            em.flush();
            return perfume.getId();
        });
    }

    @Test
    void calculoMonetarioYLimitesDeDescuento() {
        assertEquals(new BigDecimal("76.00"), PrecioPerfume.calcular(95.0, 20));
        assertEquals(new BigDecimal("5.03"), PrecioPerfume.calcular(10.05, 50));
        assertEquals(new BigDecimal("95.00"), PrecioPerfume.calcular(95.0, 0));
        assertEquals(new BigDecimal("0.00"), PrecioPerfume.calcular(95.0, 100));
        assertThrows(IllegalArgumentException.class, () -> PrecioPerfume.calcular(95, -1));
        assertThrows(IllegalArgumentException.class, () -> PrecioPerfume.calcular(95, 101));
        assertThrows(IllegalArgumentException.class, () -> PrecioPerfume.calcular(-1, 10));
    }

    @Test
    void busquedaYOrdenacionUtilizanPrecioConDescuento() {
        String prefijo = "QA Oferta " + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        crearPerfume(prefijo + " A", 100.0, 30);  // 70 EUR
        crearPerfume(prefijo + " B", 80.0, 0);    // 80 EUR

        PaginaPerfumes resultado = perfumes.buscarPerfumes(
                prefijo, null, 65.0, 75.0, "precioAsc", 1);
        assertEquals(1, resultado.total());
        assertEquals(70.0, resultado.perfumes().get(0).precio(), 0.001);
        assertEquals(100.0, resultado.perfumes().get(0).precioOriginal(), 0.001);
        assertEquals(30, resultado.perfumes().get(0).descuentoPorcentaje());

        PaginaPerfumes ordenados = perfumes.buscarPerfumes(
                prefijo, null, null, null, "precioAsc", 1);
        assertEquals(2, ordenados.total());
        assertTrue(ordenados.perfumes().get(0).precio() <= ordenados.perfumes().get(1).precio());
        assertEquals(70.0, ordenados.perfumes().get(0).precio(), 0.001);
    }

    @Test
    void pedidoConservaImporteDespuesDeCambiarOferta() {
        String codigo = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        long idPerfume = crearPerfume("QA Pedido " + codigo, 100.0, 25);
        int idUsuario = tx.execute(status -> {
            Usuario cliente = new Usuario("qa-ofertas-" + codigo + "@example.test",
                    "demo", "Cliente", "Auditoria", "600000000");
            em.persist(cliente);
            em.flush();
            return cliente.getId();
        });

        carrito.agregarProducto(idUsuario, idPerfume, 2);
        var lineasCarrito = carrito.obtenerProductosCarrito(idUsuario);
        assertEquals(0, new BigDecimal(lineasCarrito.get(0).get("subtotal").toString())
                .compareTo(new BigDecimal("150.00")));

        pedidos.procesarPaso1("Cliente Auditoria", "Calle Prueba 1", "Madrid",
                "Madrid", "28001", "", idUsuario);
        pedidos.procesarPaso2("Titular Demo", "visa_demo", "qa@example.test", idUsuario);
        pedidos.procesarPaso3(false, idUsuario);
        pedidos.confirmarPedido(idUsuario);

        BigDecimal precioGuardado = tx.execute(status -> em.createQuery(
                "SELECT pp FROM ProductoPedido pp WHERE pp.pedido.usuario.id = :usuario " +
                        "AND pp.perfume.id = :perfume", ProductoPedido.class)
                .setParameter("usuario", idUsuario)
                .setParameter("perfume", idPerfume)
                .getSingleResult().getPrecioUnitario());
        assertEquals(0, precioGuardado.compareTo(new BigDecimal("75.00")));
        assertTrue(carrito.obtenerProductosCarrito(idUsuario).isEmpty());

        // Cambiar la oferta no debe reescribir el precio histórico del pedido.
        perfumes.aplicarDescuentoEnLote(List.of(idPerfume), 0);
        BigDecimal precioHistorico = tx.execute(status -> em.createQuery(
                "SELECT pp FROM ProductoPedido pp WHERE pp.pedido.usuario.id = :usuario " +
                        "AND pp.perfume.id = :perfume", ProductoPedido.class)
                .setParameter("usuario", idUsuario)
                .setParameter("perfume", idPerfume)
                .getSingleResult().getPrecioAplicable());
        assertEquals(0, precioHistorico.compareTo(new BigDecimal("75.00")));
        assertEquals(0, perfumes.obtenerPerfumesPorId(idPerfume)
                .getPrecioFinal().compareTo(new BigDecimal("100.00")));
    }

    @Test
    void descuentoAdministrativoSoloPostConSesionYCsrf() throws Exception {
        long idPerfume = crearPerfume("QA Admin " + UUID.randomUUID().toString().substring(0, 8), 90, 0);
        var mvc = MockMvcBuilders.webAppContextSetup(webContext).addFilters(filtroSeguridad).build();
        MockHttpSession sesion = new MockHttpSession();
        String inicio = mvc.perform(get("/").session(sesion))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var tokenHtml = Pattern.compile("<meta name=\"csrf-token\" content=\"([^\"]+)\"")
                .matcher(inicio);
        assertTrue(tokenHtml.find(), "El HTML debe incluir el token CSRF");
        String token = tokenHtml.group(1);

        mvc.perform(post("/admin/aplicarDescuento").session(sesion)
                        .header("X-CSRF-TOKEN", token)
                        .param("ids", String.valueOf(idPerfume))
                        .param("porcentajeDescuento", "25"))
                .andExpect(redirectedUrl("/loginAdmin"));

        sesion.setAttribute("admin", "ok");
        mvc.perform(get("/admin/aplicarDescuento").session(sesion))
                .andExpect(status().isMethodNotAllowed());
        mvc.perform(post("/admin/aplicarDescuento").session(sesion)
                        .param("ids", String.valueOf(idPerfume))
                        .param("porcentajeDescuento", "25"))
                .andExpect(status().isForbidden());
        mvc.perform(post("/admin/aplicarDescuento").session(sesion)
                        .header("X-CSRF-TOKEN", token)
                        .param("ids", String.valueOf(idPerfume))
                        .param("porcentajeDescuento", "25"))
                .andExpect(status().is3xxRedirection());
        assertEquals(25, perfumes.obtenerPerfumesPorId(idPerfume).getDescuentoPorcentaje());
    }
}
