
package com.leo.tienda.perfumes.responseData;

import java.util.List;
import java.util.Map;

public class ResumenPedido {

    // Datos de envío
    private String nombreCompleto;
    private String direccion;
    private String provincia;

    // Datos de pago simulado
    private String titularTarjeta;
    private String tipoTarjeta;

    // Perfumes del carrito
    private List<Map<String, Object>> perfumes;

    private double total;

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

    public List<Map<String, Object>> getPerfumes() {
        return perfumes;
    }

    public void setPerfumes(List<Map<String, Object>> perfumes) {
        this.perfumes = perfumes;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
