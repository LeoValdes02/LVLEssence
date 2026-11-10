package com.leo.tienda.perfumes;
import com.leo.tienda.perfumes.model.*;
import com.leo.tienda.perfumes.servicios.ServicioDescubrimiento;
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

@SpringBootTest(properties="spring.datasource.url=jdbc:h2:mem:valoraciones;DB_CLOSE_DELAY=-1")
@Transactional
class ValoracionesTest {
 @Autowired EntityManager em; @Autowired ServicioDescubrimiento servicio;
 @Autowired WebApplicationContext context;
 @Autowired @Qualifier("springSecurityFilterChain") Filter security;
 @Test void votosRankingYActualizacion() {
   Usuario a=new Usuario("valor1@test.example","x","Ana","Test","600000001"); Usuario b=new Usuario("valor2@test.example","x","Luis","Test","600000002"); em.persist(a);em.persist(b);
   Perfumes p=new Perfumes("Valorado Uno","Marca",100,"Floral",100,"Parfum","Unisex","Test");p.setDescuentoPorcentaje(20);em.persist(p);
   Perfumes q=new Perfumes("Valorado Dos","Marca",20,"Floral",100,"Parfum","Unisex","Test");em.persist(q);em.flush();
   assertTrue(servicio.mejorValorados().isEmpty());
   servicio.valorar(a.getId(),p.getId(),2);servicio.valorar(a.getId(),p.getId(),5);servicio.valorar(b.getId(),p.getId(),3);servicio.valorar(a.getId(),q.getId(),3);
   assertEquals(4.0, servicio.resumen(p.getId(),a.getId()).get("media"));assertEquals(2L, servicio.resumen(p.getId(),a.getId()).get("votos"));
   assertEquals(p.getId(),servicio.mejorValorados().get(0).get("id"));assertEquals(0, new java.math.BigDecimal("80").compareTo((java.math.BigDecimal)servicio.mejorValorados().get(0).get("precio")));
   assertThrows(IllegalArgumentException.class,()->servicio.valorar(a.getId(),p.getId(),0));assertThrows(IllegalArgumentException.class,()->servicio.valorar(a.getId(),p.getId(),6));
 }
 @Test void recomiendaPorFamiliaNotasYExcluyeReferencia() {
   Perfumes base=new Perfumes("Base Test","Marca",50,"Floral",100,"Parfum","Unisex","Test");base.setNotasSalida("Limon, Rosa");em.persist(base);
   Perfumes familia=new Perfumes("Familia Test","Marca",100,"Floral",100,"Parfum","Unisex","Test");familia.setDescuentoPorcentaje(20);em.persist(familia);
   Perfumes nota=new Perfumes("Nota Test","Marca",40,"Amaderado",100,"Parfum","Unisex","Test");nota.setNotasFondo("Limon");em.persist(nota);em.flush();
   var lista=servicio.recomendar(base.getId());assertNotEquals(base.getId(),lista.get(0).id());assertEquals(familia.getId(),lista.get(0).id());assertEquals(80.0,lista.get(0).precio());
   assertEquals(lista.size(),lista.stream().map(x->x.id()).distinct().count());assertTrue(lista.stream().anyMatch(x->x.id()==nota.getId()));
 }
 @Test void exigeSesionYCsrf() throws Exception {
   var mvc=MockMvcBuilders.webAppContextSetup(context).addFilters(security).build();
   var session=new MockHttpSession();
   mvc.perform(post("/restValoraciones").session(session).param("id","1").param("puntuacion","5")).andExpect(status().isForbidden());
   String html=mvc.perform(get("/").session(session)).andReturn().getResponse().getContentAsString();
   var m=java.util.regex.Pattern.compile("<meta name=\"csrf-token\" content=\"([^\"]+)\"").matcher(html);assertTrue(m.find());
   mvc.perform(post("/restValoraciones").session(session).header("X-CSRF-TOKEN",m.group(1)).param("id","1").param("puntuacion","5")).andExpect(status().isUnauthorized());
 }
}
