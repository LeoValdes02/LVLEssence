package com.leo.tienda.perfumes;

import com.leo.tienda.perfumes.model.Pedido;
import com.leo.tienda.perfumes.model.Usuario;
import com.leo.tienda.perfumes.servicios.ServicioSetUp;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:inicializaciontest;DB_CLOSE_DELAY=-1")
class InicializacionAisladaTest {
    @Autowired EntityManager em;
    @Autowired TransactionTemplate tx;
    @Autowired ServicioSetUp setup;

    @Test void independienteDeBanderaYConservaPedidos() {
        tx.executeWithoutResult(s -> {
            em.createQuery("delete from ProductoPedido").executeUpdate();
            em.createQuery("delete from Pedido").executeUpdate();
        });
        assertTrue(em.createQuery("select c from Configuracion c", com.leo.tienda.perfumes.model.Configuracion.class).getSingleResult().isRegistrosIniciales());
        setup.prepararPedidoEjemplo();
        assertEquals(1L, em.createQuery("select count(p) from Pedido p", Long.class).getSingleResult());
        assertEquals(2L, em.createQuery("select count(p) from ProductoPedido p where p.precioUnitario is not null", Long.class).getSingleResult());
        tx.executeWithoutResult(s -> {
            Usuario usuario = new Usuario("otro@example.test", "temporal", "Otro", "Cliente", "600000000");
            em.persist(usuario);
            Pedido pedido = new Pedido(); pedido.setUsuario(usuario); pedido.setEstado("Completado"); em.persist(pedido);
        });
        setup.prepararPedidoEjemplo(); setup.prepararRegistrosIniciales();
        assertEquals(2L, em.createQuery("select count(p) from Pedido p", Long.class).getSingleResult());
        assertEquals(1L, em.createQuery("select count(p) from Pedido p where p.usuario.email = 'otro@example.test'", Long.class).getSingleResult());
    }
}
