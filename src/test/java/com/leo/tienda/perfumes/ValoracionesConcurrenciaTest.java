package com.leo.tienda.perfumes;
import com.leo.tienda.perfumes.model.*;
import com.leo.tienda.perfumes.servicios.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties="spring.datasource.url=jdbc:h2:mem:votos_concurrencia;DB_CLOSE_DELAY=-1")
class ValoracionesConcurrenciaTest {
 @Autowired TransactionTemplate tx; @Autowired EntityManager em; @Autowired ServicioDescubrimiento votos;
 @Autowired ServicioPerfumes perfumes; @Autowired ServicioUsuarios usuarios;
 @Test void simultaneasYBorradosSeguros() throws Exception {
    long[] ids=tx.execute(s->{ Usuario u=new Usuario("concurrencia@test.example","x","Ana","Test","600000001");em.persist(u);Perfumes p=new Perfumes("Concurrencia Test","Marca",30,"Floral",100,"Parfum","Unisex","Test");em.persist(p);em.flush();return new long[]{u.getId(),p.getId()}; });
    var pool=Executors.newFixedThreadPool(2); var start=new CountDownLatch(1);
    try { var a=pool.submit(()->{start.await();votos.valorar((int)ids[0],ids[1],4);return null;});var b=pool.submit(()->{start.await();votos.valorar((int)ids[0],ids[1],5);return null;});start.countDown();a.get(10,TimeUnit.SECONDS);b.get(10,TimeUnit.SECONDS); } finally { pool.shutdownNow(); }
    assertEquals(1L, votos.resumen(ids[1],(int)ids[0]).get("votos"));
    perfumes.borrarPerfume(ids[1]);
    long restantes=tx.execute(s->em.createQuery("select count(v) from Valoracion v where v.usuario.id=:id",Long.class).setParameter("id",(int)ids[0]).getSingleResult());assertEquals(0,restantes);
    long nuevo=tx.execute(s->{Perfumes p=new Perfumes("Segundo Test","Marca",30,"Floral",100,"Parfum","Unisex","Test");em.persist(p);em.flush();return p.getId();});
    votos.valorar((int)ids[0],nuevo,3);usuarios.borrarUsuario((int)ids[0]);assertEquals(0L,votos.resumen(nuevo,null).get("votos"));
 }
}
