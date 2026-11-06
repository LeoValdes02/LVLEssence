
package com.leo.tienda.perfumes.serviciosImpl;

import com.leo.tienda.perfumes.constantesPedidos.EstadosPedido;
import com.leo.tienda.perfumes.model.*;
import com.leo.tienda.perfumes.servicios.ServicioPedidos;
import com.leo.tienda.perfumes.responseData.ResumenPedido;
import com.leo.tienda.perfumes.servicios.ServicioCarrito;

import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ServicioPedidosImplJPA implements ServicioPedidos {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ServicioCarrito servicioCarrito;


    // =================================================
    // ADMINISTRACIÓN
    // =================================================

    @Override
    public List<Pedido> obtenerPedidos() {

        return entityManager.createQuery(
                "SELECT p FROM Pedido p ORDER BY p.id DESC",
                Pedido.class
        ).getResultList();
    }


    @Override
    public Pedido obtenerPedidoPorId(long id) {

        return entityManager.find(Pedido.class, id);
    }


    @Override
    public void actualizarEstadoPedido(long id, String estado) {

        Pedido pedido = entityManager.find(Pedido.class, id);

        if (pedido == null) {
            throw new IllegalArgumentException(
                    "El pedido no existe"
            );
        }

        // Comprobar que el estado sea uno de los permitidos
        EstadosPedido.valueOf(estado);

        pedido.setEstado(estado);
    }

    // =================================================
    // CHECKOUT - PASO 1: DATOS DE ENVÍO
    // =================================================
    @Override
    public void procesarPaso1(
            String nombreCompleto,
            String direccion,
            String provincia,
            String ciudad,
            String codigoPostal,
            String instruccionesEntrega,
            int idUsuario) {

        validarTexto(nombreCompleto, "Nombre completo");
        validarTexto(direccion, "Dirección");
        validarTexto(provincia, "Provincia");
        validarTexto(ciudad, "Ciudad");
        validarTexto(codigoPostal, "Código postal");

        if (!codigoPostal.matches("\\d{5}")) {
            throw new IllegalArgumentException(
                    "El código postal debe tener 5 dígitos"
            );
        }

        Pedido pedido = obtenerPedidoActual(idUsuario);

        pedido.setNombreCompleto(nombreCompleto);
        pedido.setDireccion(direccion);
        pedido.setProvincia(provincia);

        pedido.setCiudad(ciudad);
        pedido.setCodigoPostal(codigoPostal);

        pedido.setInstruccionesEntrega(
                instruccionesEntrega == null ? "" : instruccionesEntrega
        );
    }

    @Override
    public void procesarPaso2(
            String titularTarjeta,
            String tipoTarjeta,
            String emailFacturacion,
            int idUsuario) {

        validarTexto(titularTarjeta, "Titular");
        validarTexto(tipoTarjeta, "Tipo de tarjeta");
        validarTexto(emailFacturacion, "Email de facturación");

        if (!emailFacturacion.matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            throw new IllegalArgumentException(
                    "Email de facturación no válido"
            );
        }

        Pedido pedido = obtenerPedidoActual(idUsuario);

        pedido.setTitularTarjeta(titularTarjeta);
        pedido.setTipoTarjeta(tipoTarjeta);
        pedido.setEmailFacturacion(emailFacturacion);
    }

    @Override
    public void procesarPaso3(
            boolean envoltorioRegalo,
            int idUsuario) {

        Pedido pedido = obtenerPedidoActual(idUsuario);

        pedido.setEnvoltorioRegalo(envoltorioRegalo);
    }


    @Override
    public ResumenPedido obtenerResumenPedido(int idUsuario) {

        // Recuperar el pedido que todavía está en proceso
        Pedido pedido = obtenerPedidoActual(idUsuario);

        // Crear el resumen
        ResumenPedido resumen = new ResumenPedido();

        resumen.setNombreCompleto(pedido.getNombreCompleto());
        resumen.setDireccion(pedido.getDireccion());
        resumen.setProvincia(pedido.getProvincia());

        resumen.setTitularTarjeta(pedido.getTitularTarjeta());
        resumen.setTipoTarjeta(pedido.getTipoTarjeta());
        resumen.setCiudad(pedido.getCiudad());
        resumen.setCodigoPostal(pedido.getCodigoPostal());
        resumen.setInstruccionesEntrega(
                pedido.getInstruccionesEntrega()
        );

        resumen.setEmailFacturacion(
                pedido.getEmailFacturacion()
        );

        resumen.setEnvoltorioRegaloTexto(
                Boolean.TRUE.equals(pedido.getEnvoltorioRegalo())
                        ? "Sí" : "No"
        );

        // Recuperar los perfumes que hay en el carrito
        List<Map<String, Object>> perfumes =
                servicioCarrito.obtenerProductosCarrito(idUsuario);

        resumen.setPerfumes(perfumes);

        // Calcular el total
        double total = 0;

        for (Map<String, Object> perfume : perfumes) {

            Number subtotal = (Number) perfume.get("subtotal");

            total += subtotal.doubleValue();
        }

        resumen.setTotal(Math.round(total * 100.0) / 100.0);

        return resumen;
    }


    // =================================================
    // CONFIRMAR PEDIDO
    // =================================================

    @Override
    public void confirmarPedido(int idUsuario) {

        Pedido pedido = obtenerPedidoActual(idUsuario);

        // Comprobar que se hayan completado los pasos
        if (pedido.getNombreCompleto() == null ||
                pedido.getDireccion() == null ||
                pedido.getProvincia() == null ||
                pedido.getCiudad() == null ||
                pedido.getCodigoPostal() == null ||
                pedido.getTitularTarjeta() == null ||
                pedido.getTipoTarjeta() == null ||
                pedido.getEmailFacturacion() == null ||
                pedido.getEnvoltorioRegalo() == null) {

            throw new IllegalStateException(
                    "Debes completar todos los pasos del checkout"
            );
        }

        // Obtener productos del carrito del usuario
        List<ProductoCarrito> productos =
                entityManager.createQuery(
                                "SELECT pc FROM ProductoCarrito pc " +
                                        "JOIN FETCH pc.perfume " +
                                        "WHERE pc.carrito.usuario.id = :idUsuario",
                                ProductoCarrito.class
                        )
                        .setParameter("idUsuario", idUsuario)
                        .getResultList();

        if (productos.isEmpty()) {
            throw new IllegalStateException(
                    "No puedes confirmar un carrito vacío"
            );
        }

        // Copiar productos del carrito al pedido
        for (ProductoCarrito productoCarrito : productos) {

            if (productoCarrito.getCantidad() < 1) {
                throw new IllegalStateException(
                        "Cantidad de producto incorrecta"
                );
            }

            ProductoPedido productoPedido = new ProductoPedido();

            productoPedido.setPedido(pedido);
            productoPedido.setPerfume(
                    productoCarrito.getPerfume()
            );
            productoPedido.setCantidad(
                    productoCarrito.getCantidad()
            );
            productoPedido.setPrecioUnitario(java.math.BigDecimal.valueOf(productoCarrito.getPerfume().getPrecio()).setScale(2, java.math.RoundingMode.HALF_UP));

            entityManager.persist(productoPedido);

            pedido.getProductosPedido().add(productoPedido);
        }

        // Vaciar el carrito
        for (ProductoCarrito productoCarrito : productos) {
            entityManager.remove(productoCarrito);
        }

        // Finalizar el pedido
        pedido.setEstado(EstadosPedido.Completado.name());
    }


    // =================================================
    // MÉTODO PRIVADO: OBTENER PEDIDO EN PROCESO
    // =================================================

    private Pedido obtenerPedidoActual(int idUsuario) {

        List<Pedido> pedidos = entityManager.createQuery(
                        "SELECT p FROM Pedido p " +
                                "WHERE p.usuario.id = :idUsuario " +
                                "AND p.estado = :estado",
                        Pedido.class
                )
                .setParameter("idUsuario", idUsuario)
                .setParameter("estado", EstadosPedido.EnProceso.name())
                .getResultList();

        if (pedidos.size() == 1) {
            return pedidos.get(0);
        }

        if (pedidos.size() > 1) {
            throw new IllegalStateException(
                    "El usuario tiene varios pedidos en proceso"
            );
        }

        // Si no existe, crear uno nuevo
        Usuario usuario = entityManager.find(
                Usuario.class, idUsuario
        );

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario no existe"
            );
        }

        Pedido nuevoPedido = new Pedido();

        nuevoPedido.setUsuario(usuario);
        nuevoPedido.setEstado(EstadosPedido.EnProceso.name());

        entityManager.persist(nuevoPedido);

        return nuevoPedido;
    }


    // =================================================
    // VALIDACIÓN BÁSICA
    // =================================================

    private void validarTexto(String texto, String campo) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(
                    campo + " es obligatorio"
            );
        }
    }
}
