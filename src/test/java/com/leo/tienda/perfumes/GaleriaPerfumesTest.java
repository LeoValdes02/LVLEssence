package com.leo.tienda.perfumes;

import com.leo.tienda.perfumes.model.*;
import com.leo.tienda.perfumes.servicios.*;
import jakarta.persistence.EntityManager;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties="spring.datasource.url=jdbc:h2:mem:galeria_test;DB_CLOSE_DELAY=-1")
@Transactional
class GaleriaPerfumesTest {
    @Autowired EntityManager em;
    @Autowired ServicioGaleria galeria;
    @Autowired ServicioPerfumes perfumes;
    @Autowired WebApplicationContext context;
    @Autowired @Qualifier("springSecurityFilterChain") Filter security;
    private byte[] imagen(String formato, int color) throws Exception {
        var b=new BufferedImage(2,2,BufferedImage.TYPE_INT_RGB); b.setRGB(0,0,color);
        var out=new ByteArrayOutputStream(); assertTrue(ImageIO.write(b,formato,out)); return out.toByteArray();
    }
    private MockMultipartFile archivo(String nombre, byte[] datos) { return new MockMultipartFile("fotosAdicionales",nombre,"application/octet-stream",datos); }
    private Perfumes perfume() { var p=new Perfumes("Galeria Test","Dior",100,"Floral",100,"Parfum","Unisex","Test"); p.setDescuentoPorcentaje(20); em.persist(p); em.flush(); return p; }
    @Test void portadaAntiguaYGaleriaOrdenadaSinBytesEnJson() throws Exception {
        Perfumes p=perfume(); p.setImagenPortada(imagen("png",0x123456)); em.flush();
        var mvc=MockMvcBuilders.webAppContextSetup(context).build();
        mvc.perform(get("/restPerfumes/galerias").param("ids",Long.toString(p.getId())))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].imagenes.length()").value(1))
            .andExpect(jsonPath("$[0].imagenes[0]").value("/mostrar_imagen?id="+p.getId()));
        galeria.actualizar(p,null,List.of(archivo("uno.jpg",imagen("jpeg",0xff0000)),archivo("dos.png",imagen("png",0xff00))),null,Map.of());em.flush();
        var meta=galeria.listar(p.getId()); assertEquals(2,meta.size());
        var resultado=galeria.galerias(List.of(p.getId()));assertEquals(3,resultado.get(0).imagenes().size());
        mvc.perform(get(resultado.get(0).imagenes().get(1))).andExpect(status().isOk()).andExpect(content().contentType("image/jpeg"))
            .andExpect(header().string("X-Content-Type-Options","nosniff"));
        galeria.actualizar(p,null,List.of(),List.of(),Map.of(meta.get(0).id(),2,meta.get(1).id(),1));em.flush();
        assertEquals(meta.get(1).id(),galeria.listar(p.getId()).get(0).id());
        galeria.actualizar(p,null,List.of(),List.of(meta.get(0).id()),Map.of());em.flush();
        assertNull(galeria.imagen(meta.get(0).id())); assertEquals(1,galeria.listar(p.getId()).size());
        assertArrayEquals(imagen("png",0x123456),p.getImagenPortada()); assertEquals(20,p.getDescuentoPorcentaje());
        perfumes.borrarPerfume(p.getId());em.flush();assertEquals(0L,em.createQuery("select count(i) from ImagenPerfume i where i.perfume.id=:id",Long.class).setParameter("id",p.getId()).getSingleResult());
    }
    @Test void validaFormatosLimitesYDuplicados() throws Exception {
        Perfumes p=perfume();byte[] png=imagen("png",0x123456);
        byte[] webp=Base64.getDecoder().decode("UklGRiIAAABXRUJQVlA4IBYAAAAwAQCdASoBAAEADsD+JaQAA3AAAAAA");
        galeria.actualizar(p,null,List.of(archivo("a.png",png),archivo("b.webp",webp)),null,null); em.flush();
        assertEquals(2,galeria.listar(p.getId()).size());
        assertThrows(IllegalArgumentException.class,()->galeria.actualizar(p,null,List.of(archivo("a.png",png)),null,null));
        assertThrows(IllegalArgumentException.class,()->galeria.actualizar(p,null,List.of(archivo("mal.png","<svg/>".getBytes(java.nio.charset.StandardCharsets.US_ASCII))),null,null));
        assertThrows(IllegalArgumentException.class,()->galeria.actualizar(p,null,List.of(archivo("grande.jpg",new byte[2*1024*1024+1])),null,null));
        byte[] falsa=webp.clone(); falsa[4]=0;assertThrows(IllegalArgumentException.class,()->galeria.actualizar(p,null,List.of(archivo("mal.webp",falsa)),null,null));
    }
    @Test void limiteYPropiedadDeImagenes() throws Exception {
        Perfumes p=perfume(); Perfumes otro=new Perfumes("Otro Perfume","Dior",30,"Floral",100,"Parfum","Unisex","Test");em.persist(otro);em.flush();
        List<MockMultipartFile> fotos=new ArrayList<>(); for(int i=0;i<5;i++) fotos.add(archivo("foto.png",imagen("png",i+1000)));
        galeria.actualizar(p,null,new ArrayList<>(fotos),null,null);em.flush();
        assertEquals(5,galeria.listar(p.getId()).size());long id=galeria.listar(p.getId()).get(0).id();
        assertThrows(IllegalArgumentException.class,()->galeria.actualizar(otro,null,List.of(),List.of(id),Map.of()));
        assertThrows(IllegalArgumentException.class,()->galeria.actualizar(p,null,List.of(archivo("extra.png",imagen("png",9000))),null,null));
    }
    @Test void formularioAdministrativoExigeSesionYCsrf() throws Exception {
        var mvc=MockMvcBuilders.webAppContextSetup(context).addFilters(security).build();var sesion=new MockHttpSession();
        mvc.perform(multipart("/admin/guardarPerfume").session(sesion)).andExpect(status().isForbidden());
        String html=mvc.perform(get("/").session(sesion)).andReturn().getResponse().getContentAsString();var m=java.util.regex.Pattern.compile("<meta name=\"csrf-token\" content=\"([^\"]+)\"").matcher(html);assertTrue(m.find());String token=m.group(1);
        mvc.perform(multipart("/admin/guardarPerfume").session(sesion).header("X-CSRF-TOKEN",token)).andExpect(redirectedUrl("/loginAdmin"));
        sesion.setAttribute("admin","ok"); var c=new Categoria("Galeria categorias","Test");em.persist(c);em.flush();
        mvc.perform(multipart("/admin/guardarPerfume").file(archivo("foto.png",imagen("png",123))).session(sesion).header("X-CSRF-TOKEN",token)
            .param("nombre","Perfume Galeria").param("marca","Dior").param("precio","100").param("descuentoPorcentaje","15")
            .param("mililitros","100").param("concentracion","Parfum").param("genero","Unisex").param("descripcion","Test").param("stock","5").param("idCategoria",Integer.toString(c.getId())))
            .andExpect(status().isOk()).andExpect(view().name("admin/producto/registro_producto_ok"));
        em.flush(); var p=em.createQuery("select p from Perfumes p where p.nombre='Perfume Galeria'",Perfumes.class).getSingleResult();
        assertEquals(1,galeria.listar(p.getId()).size());
        mvc.perform(get("/admin/editarPerfume").session(sesion).param("id",Long.toString(p.getId()))).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("ordenImagenes[")));
        long imagenId=galeria.listar(p.getId()).get(0).id();
        mvc.perform(multipart("/admin/guardaCambiosrPerfume").session(sesion).header("X-CSRF-TOKEN",token)
            .param("id",Long.toString(p.getId())).param("nombre","Perfume Galeria").param("marca","Dior").param("precio","100").param("descuentoPorcentaje","15")
            .param("mililitros","100").param("concentracion","Parfum").param("genero","Unisex").param("descripcion","Test").param("stock","5").param("idCategoria",Integer.toString(c.getId()))
            .param("ordenImagenes["+imagenId+"]","2").param("eliminarImagenes",Long.toString(imagenId)))
            .andExpect(status().is3xxRedirection());
        em.flush(); assertTrue(galeria.listar(p.getId()).isEmpty()); assertEquals(15,p.getDescuentoPorcentaje()); assertEquals(c.getId(),p.getCategoria().getId());
    }
}
