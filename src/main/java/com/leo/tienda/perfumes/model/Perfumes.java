package com.leo.tienda.perfumes.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import com.leo.tienda.perfumes.precios.PrecioPerfume;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "tabla_perfumes")
public class Perfumes {

    // una forma muy comoda e interesante para hacer validaciones
    // es usar un standar que se llama JSR
    // se basa en poner anotaciones de validacion a cada campo

    @Size(min = 3, max = 40, message = "Titulo debe tener entre 3 y 40 caracteres")
    @NotEmpty(message = "Debes insertar un titulo")
    // una expresion regular es una forma de indicar que queremos o no en un campo de entrada de usuario
    @Pattern(regexp = "[a-zçáéíóúñA-ZÁÉÍÓÚÑ 0-9]+", message = "titulo solo puede tener letras numeros y espacios")


    private String nombre;
    private String marca;
    @NotNull(message = "debes indicar un precio")
    @Min(value = 1, message = "Precio minimo 1 euro")
    @Max(value = 9999, message = "Precio minimo 1 euro")
    private double precio;

    // Integer (no int): los perfumes anteriores pueden tener NULL tras añadir la columna.
    // En toda lectura, NULL equivale a no tener descuento.
    @Min(value = 0, message = "El descuento no puede ser negativo")
    @Max(value = 100, message = "El descuento no puede superar el 100 %")
    @Column(name = "descuento_porcentaje")
    private Integer descuentoPorcentaje = 0;
    private String familiaOlfativa;
    private int mililitros;
    private String concentracion;
    private String genero;
    private String descripcion;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // este campo va a ser solo
    // para gestionar la subida de archivo, el input type="file"
    // esto es solo para recibir facilmente la imagen del form
    // @Transient es para decir que el campo sea ignorado de cara a la base de datos
    @Transient
    private MultipartFile fotoSubida;

    // el siguiente campo es para almacenar la imagen
    // en base de datos
    @Lob // para tipos de columna, p.ej blob o clob, para guardar imagenes/archivos/arrays de bytes
    private byte[] imagenPortada;

    @ManyToOne
    private Categoria categoria;

    //para gestionar la id del form y para otros usos
    @Transient
    private int idCategoria;

    public Perfumes() {
    }

    public Perfumes(String nombre, String marca, double precio, String familiaOlfativa, int mililitros, String concentracion, String genero, String descripcion) {
        this.nombre = nombre;
        this.marca = marca;
        this.precio = precio;
        this.familiaOlfativa = familiaOlfativa;
        this.mililitros = mililitros;
        this.concentracion = concentracion;
        this.genero = genero;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getDescuentoPorcentaje() {
        return descuentoPorcentaje == null ? 0 : descuentoPorcentaje;
    }

    public void setDescuentoPorcentaje(Integer descuentoPorcentaje) {
        this.descuentoPorcentaje = descuentoPorcentaje == null ? 0 : descuentoPorcentaje;
    }

    @Transient
    public BigDecimal getPrecioFinal() {
        return PrecioPerfume.calcular(precio, getDescuentoPorcentaje());
    }

    @Transient
    public boolean isEnOferta() {
        return getDescuentoPorcentaje() > 0;
    }

    public String getFamiliaOlfativa() {
        return familiaOlfativa;
    }

    public void setFamiliaOlfativa(String familiaOlfativa) {
        this.familiaOlfativa = familiaOlfativa;
    }

    public int getMililitros() {
        return mililitros;
    }

    public void setMililitros(int mililitros) {
        this.mililitros = mililitros;
    }

    public String getConcentracion() {
        return concentracion;
    }

    public void setConcentracion(String concentracion) {
        this.concentracion = concentracion;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MultipartFile getFotoSubida() {
        return fotoSubida;
    }

    public void setFotoSubida(MultipartFile fotoSubida) {
        this.fotoSubida = fotoSubida;
    }

    public byte[] getImagenPortada() {
        return imagenPortada;
    }

    public void setImagenPortada(byte[] imagenPortada) {
        this.imagenPortada = imagenPortada;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    @Override
    public String toString() {
        return "Perfume{" + "nombre='" + nombre + '\'' + ", marca='" + marca + '\'' + ", precio=" + precio + ", familiaOlfativa='" + familiaOlfativa + '\'' + ", mililitros=" + mililitros + ", concentracion='" + concentracion + '\'' + ", genero='" + genero + '\'' + ", descripcion='" + descripcion + '\'' + ", id=" + id + '}';
    }
}