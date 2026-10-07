package com.leo.tienda.perfumes.serviciosImpl;

import com.leo.tienda.perfumes.model.Perfume;
import com.leo.tienda.perfumes.servicios.ServicioPerfumes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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
    public void registrarPerfume(Perfume p) {
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
        return entityManager.createQuery("select p from Perfume p").getResultList();
    }

    @Override
    public Perfume obtenerPerfumesPorId(long id) {
        return entityManager.find(Perfume.class, id);
    }

    @Override
    public void borrarPerfume(long id) throws Exception {

        //En JPA la forma mas comun de borrar un registro
        // es pedirlo, comprobar que existe y luego borrarlo

        Perfume perfumeAborrar = entityManager.find(Perfume.class, id);
        if (perfumeAborrar != null) {
            entityManager.remove(perfumeAborrar);

        } else {
            throw new Exception("id no encontrado");
        }

    }

    @Override
    public void guardarCambiosPerfume(Perfume perfumeEditar) throws Exception {

//      para no perder la foto del perfume, debemos ver si el usuario
//      le ha asignado una nueva foto en el caso contrario le cargamos la anterior
        if (perfumeEditar.getFotoSubida().getSize() == 0) {
            Perfume original =
                    entityManager.find(Perfume.class, perfumeEditar.getId());
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
        return List.of();
    }
}
