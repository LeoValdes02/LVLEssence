package com.leo.tienda.perfumes.serviciosImpl;
import com.leo.tienda.perfumes.model.*;
import com.leo.tienda.perfumes.responseData.PerfumeResumen;
import com.leo.tienda.perfumes.servicios.ServicioFavoritos;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service @Transactional
public class ServicioFavoritosImplJPA implements ServicioFavoritos {
    @PersistenceContext private EntityManager em;
    public List<PerfumeResumen> listar(int usuario) {
        return em.createQuery("select new com.leo.tienda.perfumes.responseData.PerfumeResumen(p.id, p.nombre, p.marca, round(p.precio * (100 - coalesce(p.descuentoPorcentaje, 0)) / 100.0, 2), p.precio, coalesce(p.descuentoPorcentaje, 0), c.nombre, p.familiaOlfativa) from Favorito f join f.perfume p left join p.categoria c where f.usuario.id = :usuario order by p.nombre, p.id", PerfumeResumen.class).setParameter("usuario", usuario).getResultList();
    }
    public void agregar(int usuario, long perfume) {
        Usuario u = em.find(Usuario.class, usuario); Perfumes p = em.find(Perfumes.class, perfume);
        if (u == null || p == null) throw new IllegalArgumentException("Perfume o usuario no encontrado");
        Long cantidad = em.createQuery("select count(f) from Favorito f where f.usuario.id = :u and f.perfume.id = :p", Long.class).setParameter("u", usuario).setParameter("p", perfume).getSingleResult();
        if (cantidad == 0) { em.persist(new Favorito(u, p)); em.flush(); }
    }
    public void eliminar(int usuario, long perfume) {
        em.createQuery("delete from Favorito f where f.usuario.id = :u and f.perfume.id = :p").setParameter("u", usuario).setParameter("p", perfume).executeUpdate();
    }
}
