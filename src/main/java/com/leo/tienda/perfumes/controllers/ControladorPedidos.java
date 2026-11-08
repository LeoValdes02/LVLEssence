package com.leo.tienda.perfumes.controllers;

import com.leo.tienda.perfumes.constantesPedidos.EstadosPedido;
import com.leo.tienda.perfumes.model.Pedido;
import com.leo.tienda.perfumes.model.ProductoPedido;
import com.leo.tienda.perfumes.servicios.ServicioPedidos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Controller
@RequestMapping("/admin")
public class ControladorPedidos {

    @Autowired
    private ServicioPedidos servicioPedidos;

    // LISTAR TODOS LOS PEDIDOS
    @GetMapping("/gestionarPedidos")
    public String gestionarPedidos(Model model) {

        model.addAttribute("pedidos", servicioPedidos.obtenerPedidos());

        return "admin/gestionar_pedidos";
    }

    // VER EL DETALLE DE UN PEDIDO
    @GetMapping("/verDetallesPedido")
    public String verDetallesPedido(@RequestParam("id") long id, Model model) {

        Pedido pedido = servicioPedidos.obtenerPedidoPorId(id);

        if (pedido == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El pedido no existe");
        }

        BigDecimal total = BigDecimal.ZERO;

        for (ProductoPedido producto : pedido.getProductosPedido()) {

            BigDecimal precio = producto.getPrecioAplicable();

            BigDecimal cantidad = BigDecimal.valueOf(producto.getCantidad());

            total = total.add(precio.multiply(cantidad));
        }

        total = total.setScale(2, RoundingMode.HALF_UP);

        model.addAttribute("pedido", pedido);
        model.addAttribute("total", total);
        model.addAttribute("estadosDisponibles",
                EstadosPedido.destinosAdministracion(pedido.getEstado()));

        return "admin/pedidos_detalle";
    }

    // Cambiar estado: solo POST (interceptor de admin + protección CSRF de Spring Security).
    @PostMapping("/actualizarEstadoPedido")
    public String actualizarEstadoPedido(@RequestParam("id") long id,
                                         @RequestParam("estado") String estado,
                                         RedirectAttributes redirectAttributes) {
        try {
            servicioPedidos.actualizarEstadoPedido(id, estado);
            redirectAttributes.addFlashAttribute("mensaje", "Estado del pedido actualizado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        // PRG: redirección para evitar que actualizar la página repita el POST.
        redirectAttributes.addAttribute("id", id);
        return "redirect:/admin/verDetallesPedido";
    }
}
