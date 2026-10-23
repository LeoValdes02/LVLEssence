
package com.leo.tienda.perfumes.controladoresREST;

import com.leo.tienda.perfumes.model.Usuario;
import com.leo.tienda.perfumes.servicios.ServicioCarrito;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/restCarrito")
public class CarritoREST {

    @Autowired
    private ServicioCarrito servicioCarrito;

    private int obtenerUsuarioIdentificado(HttpServletRequest request) {

        HttpSession sesion = request.getSession(false);

        if (sesion == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Debes identificarte");
        }

        Usuario usuario = (Usuario)
                sesion.getAttribute("usuario_identificado");

        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Debes identificarte");
        }

        return usuario.getId();
    }

    @PostMapping("/agregarProducto")
    public String agregarProducto(
            @RequestParam("id") long idPerfume,
            @RequestParam("cantidad") int cantidad,
            HttpServletRequest request) {

        if (cantidad < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Cantidad no válida");
        }

        int idUsuario = obtenerUsuarioIdentificado(request);

        try {
            servicioCarrito.agregarProducto(
                    idUsuario, idPerfume, cantidad);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return "Perfume añadido al carrito";
    }

    @GetMapping("/obtenerProductos")
    public List<Map<String, Object>> obtenerProductos(
            HttpServletRequest request) {

        int idUsuario = obtenerUsuarioIdentificado(request);
        return servicioCarrito.obtenerProductosCarrito(idUsuario);
    }

    @PostMapping("/borrarProducto")
    public String borrarProducto(
            @RequestParam("id") long idPerfume,
            HttpServletRequest request) {

        int idUsuario = obtenerUsuarioIdentificado(request);

        servicioCarrito.borrarProducto(idUsuario, idPerfume);

        return "Perfume eliminado del carrito";
    }

    @PostMapping("/cambiarCantidad")
    public String cambiarCantidad(
            @RequestParam("id") long idPerfume,
            @RequestParam("cantidad") int cantidad,
            HttpServletRequest request) {

        int idUsuario = obtenerUsuarioIdentificado(request);

        try {
            servicioCarrito.cambiarCantidad(
                    idUsuario, idPerfume, cantidad);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return "Cantidad actualizada";
    }
}
