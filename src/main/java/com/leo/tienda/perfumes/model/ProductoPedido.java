
package com.leo.tienda.perfumes.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class ProductoPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "perfume_id", nullable = false)
    private Perfumes perfume;

    private int cantidad;

    @Column(precision = 19, scale = 2)
    private BigDecimal precioUnitario;

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getPrecioAplicable() {
        // Los pedidos anteriores sin precio guardado muestran una estimación del catálogo actual.
        return precioUnitario != null ? precioUnitario : BigDecimal.valueOf(perfume.getPrecio());
    }

    public ProductoPedido() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Perfumes getPerfume() {
        return perfume;
    }

    public void setPerfume(Perfumes perfume) {
        this.perfume = perfume;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
