package com.leo.tienda.perfumes.serviciosImpl;

import com.leo.tienda.perfumes.model.Usuario;
import com.leo.tienda.perfumes.servicios.ServicioUsuarios;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ServicioUsuariosImplJPA implements ServicioUsuarios {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Usuario> obtenerUsuarios() {
        return entityManager.createQuery("select u from Usuario u", Usuario.class).getResultList();
    }

    @Override
    public void registrarUsuario(Usuario usuario) {
        entityManager.persist(usuario);
    }

    @Override
    public void borrarUsuario(int id) throws Exception {
        Usuario usuarioABorrar = entityManager.find(Usuario.class, id);
        if (usuarioABorrar != null) {
            entityManager.remove(usuarioABorrar);
        } else {
            throw new Exception("id no encontrado");
        }
    }

    @Override
    public Usuario obtenerUsuarioPorId(int id) {
        return entityManager.find(Usuario.class, id);
    }

    @Override
    public Usuario obtenerUsuarioPorEmailYpass(String email, String pass) {

        List<Usuario> resultados = entityManager.createQuery(
                        "SELECT u FROM Usuario u WHERE u.email = :email AND u.pass = :pass",
                        Usuario.class
                )
                .setParameter("email", email)
                .setParameter("pass", pass)
                .getResultList();

        if (resultados.isEmpty()) {
            return null;
        }

        return resultados.get(0);
    }

    @Override
    public void guardarCambiosUsuario(Usuario usuarioEditar) throws Exception {
        entityManager.merge(usuarioEditar);
    }
}
