
package com.leo.tienda.perfumes.servicios;

import com.leo.tienda.perfumes.model.Pedido;
import com.leo.tienda.perfumes.responseData.ResumenPedido;

import java.util.List;

public interface ServicioPedidos {

    // Administración
    List<Pedido> obtenerPedidos();

    Pedido obtenerPedidoPorId(long id);

    void actualizarEstadoPedido(long id, String estado);

    // Checkout: paso 1
    void procesarPaso1(
            String nombreCompleto,
            String direccion,
            String provincia,
            String ciudad,
            String codigoPostal,
            String instruccionesEntrega,
            int idUsuario
    );

    // Checkout: paso 2
    void procesarPaso2(
            String titularTarjeta,
            String tipoTarjeta,
            String emailFacturacion,
            int idUsuario
    );

    // Checkout: nuevo paso 3
    void procesarPaso3(
            boolean envoltorioRegalo,
            int idUsuario
    );

    // Resumen y confirmación
    ResumenPedido obtenerResumenPedido(int idUsuario);

    void confirmarPedido(int idUsuario);
}
