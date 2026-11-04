
package com.leo.tienda.perfumes.model;

import com.leo.tienda.perfumes.constantesPedidos.EstadosPedido;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", fetch = FetchType.EAGER)
    private List<ProductoPedido> productosPedido =
            new ArrayList<ProductoPedido>();

    private String estado = EstadosPedido.EnProceso.name();

    // DATOS DE ENVIO - PASO 1
    private String nombreCompleto;
    private String direccion;
    private String provincia;
    private String ciudad;
    private String codigoPostal;

    @Column(length = 500)
    private String instruccionesEntrega;

    // DATOS DE PAGO - PASO 2
    private String titularTarjeta;
    private String tipoTarjeta;
    private String emailFacturacion;
    private Boolean envoltorioRegalo;

    public Pedido() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<ProductoPedido> getProductosPedido() {
        return productosPedido;
    }

    public void setProductosPedido(
            List<ProductoPedido> productosPedido) {
        this.productosPedido = productosPedido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getTitularTarjeta() {
        return titularTarjeta;
    }

    public void setTitularTarjeta(String titularTarjeta) {
        this.titularTarjeta = titularTarjeta;
    }

    public String getTipoTarjeta() {
        return tipoTarjeta;
    }

    public void setTipoTarjeta(String tipoTarjeta) {
        this.tipoTarjeta = tipoTarjeta;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getInstruccionesEntrega() {
        return instruccionesEntrega;
    }

    public void setInstruccionesEntrega(String instruccionesEntrega) {
        this.instruccionesEntrega = instruccionesEntrega;
    }

    public String getEmailFacturacion() {
        return emailFacturacion;
    }

    public void setEmailFacturacion(String emailFacturacion) {
        this.emailFacturacion = emailFacturacion;
    }

    public Boolean getEnvoltorioRegalo() {
        return envoltorioRegalo;
    }

    public void setEnvoltorioRegalo(Boolean envoltorioRegalo) {
        this.envoltorioRegalo = envoltorioRegalo;
    }


}


