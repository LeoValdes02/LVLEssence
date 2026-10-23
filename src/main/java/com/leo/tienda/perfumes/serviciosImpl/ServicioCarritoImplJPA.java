
package com.leo.tienda.perfumes.serviciosImpl;

import com.leo.tienda.perfumes.model.Carrito;
import com.leo.tienda.perfumes.model.Perfumes;
import com.leo.tienda.perfumes.model.ProductoCarrito;
import com.leo.tienda.perfumes.model.Usuario;
import com.leo.tienda.perfumes.servicios.ServicioCarrito;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ServicioCarritoImplJPA implements ServicioCarrito {

    @PersistenceContext
    private EntityManager entityManager;

    private Carrito buscarCarrito(int idUsuario) {
        List<Carrito> resultado = entityManager.createQuery(
                        "SELECT c FROM Carrito c WHERE c.usuario.id = :idUsuario",
                        Carrito.class
                )
                .setParameter("idUsuario", idUsuario)
                .getResultList();

        return resultado.isEmpty() ? null : resultado.get(0);
    }

    private List<ProductoCarrito> buscarProducto(
            int idUsuario, long idPerfume) {

        return entityManager.createQuery(
                        "SELECT pc FROM ProductoCarrito pc " +
                                "WHERE pc.carrito.usuario.id = :idUsuario " +
                                "AND pc.perfume.id = :idPerfume",
                        ProductoCarrito.class
                )
                .setParameter("idUsuario", idUsuario)
                .setParameter("idPerfume", idPerfume)
                .getResultList();
    }

    @Override
    public void agregarProducto(
            int idUsuario, long idPerfume, int cantidad) {

        if (cantidad < 1) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero");
        }

        Usuario usuario = entityManager.find(Usuario.class, idUsuario);
        Perfumes perfume = entityManager.find(Perfumes.class, idPerfume);

        if (usuario == null || perfume == null) {
            throw new IllegalArgumentException(
                    "Usuario o perfume inexistente");
        }

        Carrito carrito = buscarCarrito(idUsuario);

        if (carrito == null) {
            carrito = new Carrito();
            carrito.setUsuario(usuario);
            entityManager.persist(carrito);
            entityManager.flush();
        }

        List<ProductoCarrito> existentes =
                buscarProducto(idUsuario, idPerfume);

        if (!existentes.isEmpty()) {
            ProductoCarrito pc = existentes.get(0);
            pc.setCantidad(pc.getCantidad() + cantidad);
        } else {
            ProductoCarrito pc = new ProductoCarrito();
            pc.setCarrito(carrito);
            pc.setPerfume(perfume);
            pc.setCantidad(cantidad);
            entityManager.persist(pc);
        }
    }

    @Override
    public List<Map<String, Object>> obtenerProductosCarrito(
            int idUsuario) {

        Carrito carrito = buscarCarrito(idUsuario);
        List<Map<String, Object>> resultado = new ArrayList<>();

        if (carrito == null) {
            return resultado;
        }

        List<ProductoCarrito> productos = entityManager.createQuery(
                        "SELECT pc FROM ProductoCarrito pc " +
                                "JOIN FETCH pc.perfume " +
                                "WHERE pc.carrito.id = :idCarrito " +
                                "ORDER BY pc.id",
                        ProductoCarrito.class
                )
                .setParameter("idCarrito", carrito.getId())
                .getResultList();

        for (ProductoCarrito pc : productos) {
            Perfumes p = pc.getPerfume();

            Map<String, Object> datos = new LinkedHashMap<>();

            datos.put("id", p.getId());
            datos.put("nombre", p.getNombre());
            datos.put("marca", p.getMarca());
            datos.put("precio", p.getPrecio());
            datos.put("cantidad", pc.getCantidad());

            double subtotal = p.getPrecio() * pc.getCantidad();
            datos.put("subtotal",
                    Math.round(subtotal * 100.0) / 100.0);

            resultado.add(datos);
        }

        return resultado;
    }

    @Override
    public void borrarProducto(int idUsuario, long idPerfume) {

        List<ProductoCarrito> productos =
                buscarProducto(idUsuario, idPerfume);

        for (ProductoCarrito pc : productos) {
            entityManager.remove(pc);
        }
    }

    @Override
    public void cambiarCantidad(
            int idUsuario, long idPerfume, int cantidad) {

        if (cantidad < 1) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero");
        }

        List<ProductoCarrito> productos =
                buscarProducto(idUsuario, idPerfume);

        if (productos.isEmpty()) {
            throw new IllegalArgumentException(
                    "El perfume no está en el carrito");
        }

        productos.get(0).setCantidad(cantidad);
    }
}
