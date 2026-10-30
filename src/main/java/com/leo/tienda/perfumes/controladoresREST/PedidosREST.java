
package com.leo.tienda.perfumes.controladoresREST;

import com.leo.tienda.perfumes.model.Usuario;
import com.leo.tienda.perfumes.responseData.ResumenPedido;
import com.leo.tienda.perfumes.servicios.ServicioPedidos;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/restPedido")
public class PedidosREST {

    @Autowired
    private ServicioPedidos servicioPedidos;

    // Obtener el usuario identificado
    private int obtenerIdUsuario(HttpServletRequest request) {

        HttpSession sesion = request.getSession(false);

        if (sesion == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debes iniciar sesión"
            );
        }

        Usuario usuario = (Usuario)
                sesion.getAttribute("usuario_identificado");

        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debes iniciar sesión"
            );
        }

        return usuario.getId();
    }

    // PASO 1 - DATOS DE ENVÍO
    @PostMapping("/procesarPaso1")
    public String procesarPaso1(
            @RequestParam("nombre") String nombreCompleto,
            @RequestParam("direccion") String direccion,
            @RequestParam("provincia") String provincia,
            HttpServletRequest request) {

        int idUsuario = obtenerIdUsuario(request);

        try {
            servicioPedidos.procesarPaso1(
                    nombreCompleto,
                    direccion,
                    provincia,
                    idUsuario
            );

            return "ok";

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage()
            );
        }
    }

    // PASO 2 - DATOS DE PAGO SIMULADO
    @PostMapping("/procesarPaso2")
    public ResumenPedido procesarPaso2(
            @RequestParam("tarjeta") String tipoTarjeta,
            @RequestParam("titular") String titularTarjeta,
            HttpServletRequest request) {

        int idUsuario = obtenerIdUsuario(request);

        try {
            servicioPedidos.procesarPaso2(
                    titularTarjeta,
                    tipoTarjeta,
                    idUsuario
            );

            return servicioPedidos.obtenerResumenPedido(idUsuario);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage()
            );
        }
    }

    // PASO 3 - CONFIRMACIÓN
    @PostMapping("/procesarPaso3")
    public String procesarPaso3(HttpServletRequest request) {

        int idUsuario = obtenerIdUsuario(request);

        try {
            servicioPedidos.confirmarPedido(idUsuario);

            return "ok";

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage()
            );

        } catch (IllegalStateException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, e.getMessage()
            );
        }
    }
}
