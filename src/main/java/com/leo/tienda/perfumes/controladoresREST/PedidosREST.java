
package com.leo.tienda.perfumes.controladoresREST;

import com.leo.tienda.perfumes.model.Usuario;
import com.leo.tienda.perfumes.responseData.ResumenPedido;
import com.leo.tienda.perfumes.servicios.ServicioPedidos;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/restPedido")
public class PedidosREST {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<java.util.Map<String, String>> error(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(java.util.Map.of("message", ex.getReason() == null ? "No se pudo completar la operación" : ex.getReason()));
    }

    @Autowired
    private ServicioPedidos servicioPedidos;

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

    // PASO 1 - ENVÍO
    @PostMapping("/procesarPaso1")
    public String procesarPaso1(
            @RequestParam("nombre") String nombreCompleto,
            @RequestParam("direccion") String direccion,
            @RequestParam("provincia") String provincia,
            @RequestParam("ciudad") String ciudad,
            @RequestParam("codigoPostal") String codigoPostal,
            @RequestParam(
                    value = "instruccionesEntrega",
                    required = false,
                    defaultValue = ""
            ) String instruccionesEntrega,
            HttpServletRequest request) {

        int idUsuario = obtenerIdUsuario(request);

        try {
            servicioPedidos.procesarPaso1(
                    nombreCompleto,
                    direccion,
                    provincia,
                    ciudad,
                    codigoPostal,
                    instruccionesEntrega,
                    idUsuario
            );

            return "ok";

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage()
            );
        }
    }

    // PASO 2 - PAGO SIMULADO
    @PostMapping("/procesarPaso2")
    public String procesarPaso2(
            @RequestParam("tarjeta") String tipoTarjeta,
            @RequestParam("titular") String titularTarjeta,
            @RequestParam("emailFacturacion") String emailFacturacion,
            HttpServletRequest request) {

        int idUsuario = obtenerIdUsuario(request);

        try {
            servicioPedidos.procesarPaso2(
                    titularTarjeta,
                    tipoTarjeta,
                    emailFacturacion,
                    idUsuario
            );

            return "ok";

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage()
            );
        }
    }

    // PASO 3 - ENVOLTORIO DE REGALO
    @PostMapping("/procesarPaso3")
    public ResumenPedido procesarPaso3(
            @RequestParam("envoltorioRegalo") String respuesta,
            HttpServletRequest request) {

        int idUsuario = obtenerIdUsuario(request);

        if (!respuesta.equals("si") && !respuesta.equals("no")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Respuesta no válida"
            );
        }

        servicioPedidos.procesarPaso3(
                respuesta.equals("si"),
                idUsuario
        );

        return servicioPedidos.obtenerResumenPedido(idUsuario);
    }

    // PASO 4 - CONFIRMAR PEDIDO
    @PostMapping("/procesarPaso4")
    public String procesarPaso4(HttpServletRequest request) {

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
