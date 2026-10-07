package com.leo.tienda.perfumes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Categoria {

    private String nombre;
    private String descripcion;

    @JsonIgnore
    @OneToMany(mappedBy = "categoria")
    private List<Perfume> perfumes = new ArrayList<Perfume>();

    @Id
    @GeneratedValue
    private int id;

    public Categoria() {
    }

    public Categoria(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Perfume> getPerfumes() {
        return perfumes;
    }

    public void setPerfumes(List<Perfume> perfumes) {
        this.perfumes = perfumes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}