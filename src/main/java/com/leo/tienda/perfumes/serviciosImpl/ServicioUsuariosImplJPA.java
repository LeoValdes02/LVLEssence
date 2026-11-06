package com.leo.tienda.perfumes.serviciosImpl;

import com.leo.tienda.perfumes.model.Carrito;
import com.leo.tienda.perfumes.model.Usuario;
import com.leo.tienda.perfumes.servicios.ServicioUsuarios;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class ServicioUsuariosImplJPA implements ServicioUsuarios {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public List<Usuario> obtenerUsuarios() {
        return entityManager.createQuery("select u from Usuario u", Usuario.class).getResultList();
    }


    @Override
    public void registrarUsuario(Usuario usuario) {

        String email = normalizarEmail(usuario.getEmail());

        Long existentes = entityManager.createQuery("SELECT COUNT(u) FROM Usuario u " + "WHERE LOWER(TRIM(u.email)) = :email", Long.class).setParameter("email", email).getSingleResult();

        if (existentes > 0) {
            throw new IllegalArgumentException("Este correo electrónico ya está registrado");
        }

        usuario.setEmail(email);
        if (usuario.getPass() == null || usuario.getPass().isBlank()) throw new IllegalArgumentException("La contraseña es obligatoria");
        usuario.setPass(passwordEncoder.encode(usuario.getPass()));
        usuario.setPassCodificada(true);
        entityManager.persist(usuario);
    }

    @Override
    public void guardarCambiosUsuario(Usuario usuarioEditar) throws Exception {

        String email = normalizarEmail(usuarioEditar.getEmail());

        Long existentes = entityManager.createQuery("SELECT COUNT(u) FROM Usuario u " + "WHERE LOWER(TRIM(u.email)) = :email " + "AND u.id <> :id", Long.class).setParameter("email", email).setParameter("id", usuarioEditar.getId()).getSingleResult();

        if (existentes > 0) {
            throw new IllegalArgumentException("Este correo electrónico ya pertenece a otro usuario");
        }

        Usuario original = entityManager.find(Usuario.class, usuarioEditar.getId());
        if (original == null) throw new IllegalArgumentException("El usuario no existe");
        if (usuarioEditar.getPass() == null || usuarioEditar.getPass().isBlank()) {
            usuarioEditar.setPass(original.getPass());
            usuarioEditar.setPassCodificada(original.getPassCodificada());
        } else {
            usuarioEditar.setPass(passwordEncoder.encode(usuarioEditar.getPass()));
            usuarioEditar.setPassCodificada(true);
        }
        usuarioEditar.setEmail(email);
        entityManager.merge(usuarioEditar);
    }

    private String normalizarEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }


    @Override
    public void borrarUsuario(int id) throws Exception {

        Usuario usuario = entityManager.find(Usuario.class, id);

        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no existe");
        }

        // No borrar usuarios con pedidos asociados
        Long totalPedidos = entityManager.createQuery("SELECT COUNT(p) FROM Pedido p " + "WHERE p.usuario.id = :id", Long.class).setParameter("id", id).getSingleResult();

        if (totalPedidos > 0) {
            throw new IllegalStateException("No se puede borrar este usuario porque tiene pedidos registrados.");
        }

        // Buscar el carrito, si existe
        List<Carrito> carritos = entityManager.createQuery("SELECT c FROM Carrito c " + "WHERE c.usuario.id = :id", Carrito.class).setParameter("id", id).getResultList();

        for (Carrito carrito : carritos) {

            // Primero borrar los productos del carrito
            entityManager.createQuery("DELETE FROM ProductoCarrito pc " + "WHERE pc.carrito.id = :idCarrito").setParameter("idCarrito", carrito.getId()).executeUpdate();

            // Después borrar el carrito
            entityManager.remove(carrito);
        }

        // Finalmente borrar el usuario
        entityManager.remove(usuario);
    }


    @Override
    public Usuario obtenerUsuarioPorId(int id) {
        return entityManager.find(Usuario.class, id);
    }

    @Override
    public Usuario obtenerUsuarioPorEmailYpass(String email, String pass) {

        if (email == null || email.isBlank() || pass == null) return null;
        List<Usuario> resultados = entityManager.createQuery("SELECT u FROM Usuario u WHERE LOWER(TRIM(u.email)) = :email", Usuario.class).setParameter("email", normalizarEmail(email)).getResultList();

        if (resultados.isEmpty()) {
            return null;
        }

        if (resultados.size() != 1) return null;
        Usuario usuario = resultados.get(0);
        if (Boolean.TRUE.equals(usuario.getPassCodificada())) {
            return passwordEncoder.matches(pass, usuario.getPass()) ? usuario : null;
        }
        // Una cuenta antigua solo se migra tras comprobar su contraseña existente.
        if (!java.util.Objects.equals(pass, usuario.getPass())) return null;
        usuario.setPass(passwordEncoder.encode(pass));
        usuario.setPassCodificada(true);
        return usuario;
    }


}
