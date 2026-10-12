package com.leo.tienda.perfumes.serviciosImpl;


import com.leo.tienda.perfumes.model.Categoria;
import com.leo.tienda.perfumes.servicios.ServicioCategorias;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class ServicioCategoriasImplJPA implements ServicioCategorias {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Categoria> obtenerCategorias() {
        return entityManager.createQuery("select c from Categoria c", Categoria.class).getResultList();
    }

    @Override
    public void registrarCategoria(Categoria c) {
        entityManager.persist(c);
    }

    @Override
    public void borrarCategoria(long id) throws Exception {
        //En JPA la forma mas comun de borrar un registro
        // es pedirlo, comprobar que existe y luego borrarlo

        Categoria categoriaAborrar = entityManager.find(Categoria.class, id);
        if (categoriaAborrar != null) {
            entityManager.remove(categoriaAborrar);

        } else {
            throw new Exception("id no encontrado");
        }

    }

    @Override
    public Categoria obtenerCategoriaPorId(long id) {
        return entityManager.find(Categoria.class, id);
    }

    @Override
    public void guardarCambiosCategoria(Categoria categoriaEditar) throws Exception {
        entityManager.merge(categoriaEditar);

    }
}
