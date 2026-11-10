package com.leo.tienda.perfumes.serviciosImpl;

import com.leo.tienda.perfumes.model.ImagenPerfume;
import com.leo.tienda.perfumes.model.Perfumes;
import com.leo.tienda.perfumes.servicios.ServicioGaleria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service @Transactional
public class ServicioGaleriaImplJPA implements ServicioGaleria {
    public static final int MAX_IMAGENES = 5;
    public static final long MAX_BYTES = 2L * 1024 * 1024;
    private static final long MAX_PIXELES = 20_000_000L;
    @PersistenceContext private EntityManager em;
    private record Archivo(byte[] bytes, String tipo, String huella) {}

    private static String huella(byte[] datos) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(datos)); }
        catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }
    private static int uint32(byte[] b, int p) {
        return (b[p]&255) | ((b[p+1]&255)<<8) | ((b[p+2]&255)<<16) | ((b[p+3]&255)<<24);
    }
    private static boolean webp(byte[] b) {
        if (b.length < 30 || !ascii(b,0,"RIFF") || !ascii(b,8,"WEBP") || Integer.toUnsignedLong(uint32(b,4)) != b.length - 8L) return false;
        int pos=12; boolean imagen=false;
        while (pos < b.length) {
            if (pos + 8 > b.length) return false;
            long len = Integer.toUnsignedLong(uint32(b,pos+4)); long fin = pos + 8L + len + (len & 1L);
            if (fin > b.length) return false;
            if (ascii(b,pos,"VP8 ")) {
                if (imagen || len < 10 || (b[pos+11]&255)!=0x9d || (b[pos+12]&255)!=0x01 || (b[pos+13]&255)!=0x2a) return false;
                int ancho=((b[pos+14]&255)|((b[pos+15]&255)<<8))&0x3fff, alto=((b[pos+16]&255)|((b[pos+17]&255)<<8))&0x3fff;
                if (ancho==0 || alto==0 || (long)ancho*alto>MAX_PIXELES) return false;
                imagen=true;
            } else if (ascii(b,pos,"VP8L")) {
                if (imagen || len < 5 || (b[pos+8]&255)!=0x2f) return false;
                int bits=uint32(b,pos+9), ancho=(bits&0x3fff)+1, alto=((bits>>>14)&0x3fff)+1;
                if ((long)ancho*alto>MAX_PIXELES || (bits>>>29)!=0) return false;
                imagen=true;
            } else if (ascii(b,pos,"VP8X")) {
                if (len != 10 || pos != 12 || (b[pos+8]&2)!=0) return false;
            } else if (!(ascii(b,pos,"ALPH") || ascii(b,pos,"ICCP") || ascii(b,pos,"EXIF") || ascii(b,pos,"XMP "))) return false;
            pos=(int)fin;
        }
        return imagen && pos==b.length;
    }
    private static boolean ascii(byte[] b,int p,String valor) {
        if (p+valor.length()>b.length) return false;
        for(int i=0;i<valor.length();i++) if ((b[p+i]&255)!=valor.charAt(i)) return false;
        return true;
    }
    private static Archivo validar(MultipartFile fichero) {
        if (fichero==null) return null;
        if (fichero.isEmpty()) {
            if (fichero.getOriginalFilename()!=null && !fichero.getOriginalFilename().isBlank()) throw new IllegalArgumentException("La imagen esta vacia");
            return null;
        }
        if (fichero.getSize()>MAX_BYTES) throw new IllegalArgumentException("Cada imagen debe ocupar como maximo 2 MB");
        try {
            byte[] b=fichero.getBytes();
            if (b.length==0 || b.length>MAX_BYTES) throw new IllegalArgumentException("Tamano de imagen invalido");
            String tipo;
            if (ascii(b,0,"RIFF")) {
                if (!webp(b)) throw new IllegalArgumentException("Imagen WebP invalida o no admitida");
                tipo="image/webp";
            } else {
                boolean jpg=b.length>=4 && (b[0]&255)==255 && (b[1]&255)==216 && (b[b.length-2]&255)==255 && (b[b.length-1]&255)==217;
                byte[] png={(byte)137,80,78,71,13,10,26,10};
                boolean esPng=b.length>=20 && Arrays.equals(Arrays.copyOf(b,8),png) && ascii(b,b.length-8,"IEND") && uint32(b,b.length-12)==0;
                if (!jpg && !esPng) throw new IllegalArgumentException("Solo se admiten imagenes JPEG, PNG o WebP validas");
                try (ImageInputStream entrada=ImageIO.createImageInputStream(new ByteArrayInputStream(b))) {
                    Iterator<ImageReader> lectores=ImageIO.getImageReaders(entrada);
                    if (!lectores.hasNext()) throw new IllegalArgumentException("Imagen invalida");
                    ImageReader lector=lectores.next();
                    try {
                        lector.setInput(entrada, true, true);
                        String formato=lector.getFormatName();
                        if ((jpg && !formato.equalsIgnoreCase("JPEG")) || (esPng && !formato.equalsIgnoreCase("PNG"))) throw new IllegalArgumentException("Formato de imagen invalido");
                        int ancho=lector.getWidth(0), alto=lector.getHeight(0);
                        if (ancho<=0 || alto<=0 || (long)ancho*alto>MAX_PIXELES || lector.read(0)==null) throw new IllegalArgumentException("Dimensiones o contenido de imagen invalidos");
                    } finally { lector.dispose(); }
                }
                tipo=jpg ? "image/jpeg" : "image/png";
            }
            return new Archivo(b,tipo,huella(b));
        } catch (IOException e) { throw new IllegalArgumentException("No se pudo leer la imagen",e); }
    }
    public void actualizar(Perfumes perfume, MultipartFile portada, List<MultipartFile> nuevas, List<Long> eliminar, Map<Long,Integer> orden) {
        if (perfume.getId()>0) em.lock(perfume,LockModeType.PESSIMISTIC_WRITE);
        List<ImagenPerfume> existentes=new ArrayList<>(perfume.getImagenesAdicionales());
        Set<Long> ids=new HashSet<>(); for (ImagenPerfume i:existentes) ids.add(i.getId());
        Set<Long> borrar=eliminar==null ? Set.of() : new HashSet<>(eliminar);
        if (!ids.containsAll(borrar) || (orden!=null && !ids.containsAll(orden.keySet()))) throw new IllegalArgumentException("Imagen no asociada al perfume");
        if (orden!=null && orden.values().stream().anyMatch(x->x==null || x<0 || x>10000)) throw new IllegalArgumentException("Orden de imagen invalido");
        List<MultipartFile> subidas=nuevas==null ? List.of() : nuevas.stream().filter(Objects::nonNull).filter(f->!f.isEmpty() || (f.getOriginalFilename()!=null && !f.getOriginalFilename().isBlank())).toList();
        if (existentes.size()-borrar.size()+subidas.size()>MAX_IMAGENES) throw new IllegalArgumentException("Maximo 5 imagenes adicionales");
        Archivo nuevaPortada=validar(portada);
        byte[] portadaFinal=nuevaPortada==null ? perfume.getImagenPortada() : nuevaPortada.bytes();
        Set<String> huellas=new HashSet<>();
        if (portadaFinal!=null && portadaFinal.length>0) huellas.add(huella(portadaFinal));
        for(ImagenPerfume i:existentes) if(!borrar.contains(i.getId()) && !huellas.add(i.getHuella())) throw new IllegalArgumentException("La portada y las imagenes adicionales no pueden repetirse");
        List<Archivo> archivos=new ArrayList<>();
        for(MultipartFile fichero:subidas) { Archivo archivo=validar(fichero); if (!huellas.add(archivo.huella())) throw new IllegalArgumentException("No se permiten imagenes duplicadas"); archivos.add(archivo); }
        if(nuevaPortada!=null) perfume.setImagenPortada(nuevaPortada.bytes());
        List<ImagenPerfume> conservar=new ArrayList<>();
        for(ImagenPerfume i:existentes) { if(borrar.contains(i.getId())) perfume.getImagenesAdicionales().remove(i); else conservar.add(i); }
        conservar.sort(Comparator.comparingInt((ImagenPerfume i)->orden==null ? i.getOrden() : orden.getOrDefault(i.getId(),i.getOrden())).thenComparing(ImagenPerfume::getId));
        int posicion=0; for(ImagenPerfume i:conservar) i.setOrden(posicion++);
        // Evita que la insercion de un reemplazo preceda al borrado con la misma huella.
        if (!borrar.isEmpty() && !archivos.isEmpty()) em.flush();
        for(Archivo f:archivos) {
            ImagenPerfume imagen = new ImagenPerfume(perfume,f.bytes(),f.tipo(),f.huella(),posicion++);
            perfume.getImagenesAdicionales().add(imagen);
            em.persist(imagen);
        }
    }
    public List<Meta> listar(long id) {
        return em.createQuery("select i.id,i.orden from ImagenPerfume i where i.perfume.id=:id order by i.orden,i.id",Object[].class).setParameter("id",id).getResultList().stream().map(f->new Meta((Long)f[0],(Integer)f[1])).toList();
    }
    public List<Galeria> galerias(List<Long> ids) {
        if(ids==null || ids.isEmpty() || ids.size()>50 || ids.stream().anyMatch(x->x==null || x<=0)) throw new IllegalArgumentException("Indica entre 1 y 50 IDs validos");
        var unicos=new LinkedHashSet<>(ids);
        @SuppressWarnings("unchecked")
        List<Object[]> perfumes=em.createNativeQuery("select id, case when imagen_portada is null or octet_length(imagen_portada)=0 then false else true end from tabla_perfumes where id in (:ids)").setParameter("ids",unicos).getResultList();
        Map<Long,List<String>> resultado=new LinkedHashMap<>();
        Map<Long,Boolean> portadas=new HashMap<>(); for(Object[] f:perfumes) portadas.put(((Number)f[0]).longValue(),(Boolean)f[1]);
        for(Long id:unicos) if(portadas.containsKey(id)) { var urls=new ArrayList<String>(); if(Boolean.TRUE.equals(portadas.get(id))) urls.add("/mostrar_imagen?id="+id); resultado.put(id,urls); }
        List<Object[]> filas=em.createQuery("select i.perfume.id,i.id from ImagenPerfume i where i.perfume.id in :ids order by i.perfume.id,i.orden,i.id",Object[].class).setParameter("ids",unicos).getResultList();
        for(Object[] f:filas) resultado.get((Long)f[0]).add("/restPerfumes/imagenes/"+f[1]);
        return resultado.entrySet().stream().map(e->new Galeria(e.getKey(),List.copyOf(e.getValue()))).toList();
    }
    public ImagenPerfume imagen(long id) { return em.find(ImagenPerfume.class,id); }
}
