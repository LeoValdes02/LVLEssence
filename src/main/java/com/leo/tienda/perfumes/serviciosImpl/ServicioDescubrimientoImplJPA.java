package com.leo.tienda.perfumes.serviciosImpl;
import com.leo.tienda.perfumes.model.*;
import com.leo.tienda.perfumes.precios.PrecioPerfume;
import com.leo.tienda.perfumes.responseData.PerfumeResumen;
import com.leo.tienda.perfumes.servicios.ServicioDescubrimiento;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.text.Normalizer;
import java.util.*;

@Service @Transactional
public class ServicioDescubrimientoImplJPA implements ServicioDescubrimiento {
    @PersistenceContext private EntityManager em;
    public void valorar(int usuario, long perfume, int puntuacion) {
        if (puntuacion < 1 || puntuacion > 5) throw new IllegalArgumentException("La puntuacion debe estar entre 1 y 5");
        Usuario u = em.find(Usuario.class, usuario, LockModeType.PESSIMISTIC_WRITE);
        Perfumes p = em.find(Perfumes.class, perfume);
        if (u == null || p == null) throw new NoSuchElementException("Perfume o usuario no encontrado");
        var existentes = em.createQuery("select v from Valoracion v where v.usuario.id=:u and v.perfume.id=:p", Valoracion.class).setParameter("u", usuario).setParameter("p", perfume).getResultList();
        if (existentes.isEmpty()) em.persist(new Valoracion(u,p,puntuacion));
        else existentes.get(0).setPuntuacion(puntuacion);
        em.flush();
    }
    public Map<String,Object> resumen(long perfume, Integer usuario) {
        if (em.find(Perfumes.class, perfume) == null) throw new NoSuchElementException("Perfume no encontrado");
        Object[] fila = em.createQuery("select avg(v.puntuacion), count(v) from Valoracion v where v.perfume.id=:p", Object[].class).setParameter("p", perfume).getSingleResult();
        var result = new LinkedHashMap<String,Object>(); result.put("media", fila[0]); result.put("votos", fila[1]);
        if (usuario != null) {
            var propios = em.createQuery("select v.puntuacion from Valoracion v where v.usuario.id=:u and v.perfume.id=:p", Integer.class).setParameter("u",usuario).setParameter("p",perfume).getResultList();
            result.put("miPuntuacion", propios.isEmpty() ? null : propios.get(0));
        } else result.put("miPuntuacion", null);
        return result;
    }
    public List<Map<String,Object>> mejorValorados() {
        var filas = em.createQuery("select p.id, p.nombre, p.marca, p.precio, p.descuentoPorcentaje, c.nombre, avg(v.puntuacion), count(v) from Valoracion v join v.perfume p left join p.categoria c group by p.id, p.nombre, p.marca, p.precio, p.descuentoPorcentaje, c.nombre order by avg(v.puntuacion) desc, count(v) desc, p.id asc", Object[].class).setMaxResults(4).getResultList();
        var result = new ArrayList<Map<String,Object>>();
        for (Object[] r: filas) { var m = new LinkedHashMap<String,Object>(); int d=r[4] == null ? 0 : (Integer)r[4]; double precio=((Number)r[3]).doubleValue();
            m.put("id",r[0]); m.put("nombre",r[1]); m.put("marca",r[2]); m.put("precio",PrecioPerfume.calcular(precio,d)); m.put("precioOriginal",precio); m.put("descuentoPorcentaje",d); m.put("categoria",r[5]); m.put("media",r[6]); m.put("votos",r[7]); result.add(m); }
        return result;
    }
    private record Candidato(PerfumeResumen resumen, String familia, String notas, Integer categoria) {}
    private static String limpiar(String s) { return s == null ? "" : Normalizer.normalize(s.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD).replaceAll("\\p{M}+", ""); }
    private static Set<String> notas(String s) { var out = new HashSet<String>(); if (s != null) for (String n:s.split(",")) { String v=limpiar(n); if (!v.isEmpty()) out.add(v); } return out; }
    public List<PerfumeResumen> recomendar(Long referencia) {
        var rows=em.createQuery("select p.id,p.nombre,p.marca,p.precio,p.descuentoPorcentaje,c.nombre,p.familiaOlfativa,p.notasSalida,p.notasCorazon,p.notasFondo,c.id from Perfumes p left join p.categoria c order by p.id",Object[].class).getResultList();
        var candidatos=new ArrayList<Candidato>();
        for(Object[] r:rows) { int d=r[4]==null?0:(Integer)r[4]; double precio=((Number)r[3]).doubleValue();
            String texto=String.join(",", Arrays.stream(new Object[]{r[7],r[8],r[9]}).filter(Objects::nonNull).map(Object::toString).toList());
            candidatos.add(new Candidato(new PerfumeResumen(((Number)r[0]).longValue(),(String)r[1],(String)r[2],PrecioPerfume.calcular(precio,d).doubleValue(),precio,d,(String)r[5]), (String)r[6],texto,(Integer)r[10])); }
        if (referencia==null) return candidatos.stream().limit(8).map(Candidato::resumen).toList();
        Candidato base=candidatos.stream().filter(c->c.resumen().id()==referencia).findFirst().orElseThrow(()->new NoSuchElementException("Perfume no encontrado"));
        Set<String> baseNotas=notas(base.notas()); String familia=limpiar(base.familia());
        Comparator<Candidato> orden=Comparator.<Candidato>comparingInt(c->!familia.isEmpty() && familia.equals(limpiar(c.familia())) ? 1:0).reversed()
            .thenComparing(Comparator.<Candidato>comparingInt(c->{ var n=notas(c.notas()); n.retainAll(baseNotas); return n.size(); }).reversed())
            .thenComparing(Comparator.<Candidato>comparingInt(c->base.categoria()!=null && base.categoria().equals(c.categoria()) ? 1:0).reversed())
            .thenComparingLong(c->c.resumen().id());
        return candidatos.stream().filter(c->c.resumen().id()!=referencia).sorted(orden).limit(8).map(Candidato::resumen).toList();
    }
}
