package com.leo.tienda.perfumes.serviciosImpl;

import com.leo.tienda.perfumes.constantesSQL.ConstantesSQL;
import com.leo.tienda.perfumes.model.Categoria;
import com.leo.tienda.perfumes.model.Perfumes;
import com.leo.tienda.perfumes.servicios.ServicioPerfumes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service //marcas que la clase actua como servicio de datos
@Transactional // los metodos de la clase se ejecutan como una unica transaccion
// Transaccion:
public class ServicioPerfumesImplJPA implements ServicioPerfumes {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void registrarPerfume(Perfumes p) {
        asignarCategoriaDesdeFormulario(p);
        //el perfume ahora mismo tiene la fotoSubida, pero
        // en imagenPortada no tiene nada.
        try {
            p.setImagenPortada(p.getFotoSubida().getBytes());
        } catch (IOException e) {
            System.out.println("NO PUDE PROCESAR LA FOTO SUBIDA");
            throw new RuntimeException(e);
        }
        entityManager.persist(p);
    }

    @Override
    public List obtenerPerfumes() {
        // lanzar JPQL
        // la consulta lanzada equivale al sql: select * from tabala_perfumes as p
        return entityManager.createQuery("select p from Perfumes p").getResultList();
    }

    @Override
    public Perfumes obtenerPerfumesPorId(long id) {
        return entityManager.find(Perfumes.class, id);
    }

    @Override
    public void borrarPerfume(long id) throws Exception {

        //En JPA la forma mas comun de borrar un registro
        // es pedirlo, comprobar que existe y luego borrarlo

        Perfumes perfumeAborrar = entityManager.find(Perfumes.class, id);
        if (perfumeAborrar != null) {
            Long pedidos = entityManager.createQuery("select count(p) from ProductoPedido p where p.perfume.id = :id", Long.class).setParameter("id", id).getSingleResult();
            Long carritos = entityManager.createQuery("select count(p) from ProductoCarrito p where p.perfume.id = :id", Long.class).setParameter("id", id).getSingleResult();
            if (pedidos > 0 || carritos > 0) throw new IllegalStateException("No se puede borrar un perfume incluido en pedidos o carritos.");
            entityManager.remove(perfumeAborrar);

        } else {
            throw new IllegalArgumentException("Perfume no encontrado");
        }

    }

    @Override
    public void guardarCambiosPerfume(Perfumes perfumeEditar) throws Exception {
        asignarCategoriaDesdeFormulario(perfumeEditar);

//      para no perder la foto del perfume, debemos ver si el usuario
//      le ha asignado una nueva foto en el caso contrario le cargamos la anterior
        if (perfumeEditar.getFotoSubida().getSize() == 0) {
            Perfumes original =
                    entityManager.find(Perfumes.class, perfumeEditar.getId());
            perfumeEditar.setImagenPortada(original.getImagenPortada());
        } else {
            perfumeEditar.setImagenPortada(perfumeEditar.getFotoSubida().getBytes());
        }


//      merge actualiza el registro ai ya existe
//       si no existe
        entityManager.merge(perfumeEditar);
    }

    @Override
    public List<Map<String, Object>> obtenerJSONperfumes() {
        return ((NativeQueryImpl) entityManager.createNativeQuery(ConstantesSQL.SQL_OBTENER_PERFUMES_JSON))
                .setTupleTransformer(AliasToEntityMapResultTransformer.INSTANCE)
                .getResultList();
    }

    @Override
    public Map<String, Object> obtenerJSONPerfumesPorId(Long id) {
        return (Map<String, Object>) ((NativeQueryImpl) entityManager.createNativeQuery(ConstantesSQL.SQL_OBTENER_PERFUME_JSON))
                .setParameter("id", id)
                .setTupleTransformer(AliasToEntityMapResultTransformer.INSTANCE)
                .getResultList()
                .get(0);
    }

    private void asignarCategoriaDesdeFormulario(Perfumes perfume) {
        if (perfume.getIdCategoria() > 0) {
            perfume.setCategoria(entityManager.find(Categoria.class, perfume.getIdCategoria()));
        }
    }
}
