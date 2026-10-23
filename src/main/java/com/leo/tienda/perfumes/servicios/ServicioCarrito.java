
package com.leo.tienda.perfumes.servicios;

import java.util.List;
import java.util.Map;

public interface ServicioCarrito {

    void agregarProducto(int idUsuario, long idPerfume, int cantidad);

    List<Map<String, Object>> obtenerProductosCarrito(int idUsuario);

    void borrarProducto(int idUsuario, long idPerfume);

    void cambiarCantidad(int idUsuario, long idPerfume, int cantidad);
}
