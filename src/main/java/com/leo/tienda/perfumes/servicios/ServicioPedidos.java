
package com.leo.tienda.perfumes.servicios;

import com.leo.tienda.perfumes.model.Pedido;
import com.leo.tienda.perfumes.responseData.ResumenPedido;

import java.util.List;

public interface ServicioPedidos {

    // Administración
    List<Pedido> obtenerPedidos();

    Pedido obtenerPedidoPorId(long id);

    void actualizarEstadoPedido(long id, String estado);

    // Checkout
    void procesarPaso1(
            String nombreCompleto,
            String direccion,
            String provincia,
            int idUsuario
    );

    void procesarPaso2(
            String titularTarjeta,
            String tipoTarjeta,
            int idUsuario
    );

    // Nuevo en el paso 3
    ResumenPedido obtenerResumenPedido(int idUsuario);

    void confirmarPedido(int idUsuario);
}
