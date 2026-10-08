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
    public List<Categoria> ObtenerCategorias() {
        return entityManager.createQuery("select c from Categoria c").getResultList();
    }
}
