package com.leo.tienda.perfumes.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Configuracion {

    private boolean registrosIniciales;

    @Id
    @GeneratedValue
    private int id;

    public boolean isRegistrosIniciales() {
        return registrosIniciales;
    }

    public void setRegistrosIniciales(boolean registrosIniciales) {
        this.registrosIniciales = registrosIniciales;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}