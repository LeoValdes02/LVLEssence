package com.leo.tienda.perfumes.constantesPedidos;

import java.util.List;

/**
 * Estados del pedido y transiciones permitidas desde administración.
 * EnProceso únicamente cambia a Completado a través de confirmarPedido().
 */
public enum EstadosPedido {
    EnProceso,
    Completado,
    Enviado,
    Incidencia;

    public static List<EstadosPedido> destinosAdministracion(String estadoActual) {
        EstadosPedido actual = EstadosPedido.valueOf(estadoActual);
        return switch (actual) {
            case EnProceso -> List.of();
            case Completado -> List.of(Enviado, Incidencia);
            case Enviado -> List.of(Incidencia);
            case Incidencia -> List.of(Completado, Enviado);
        };
    }
}
