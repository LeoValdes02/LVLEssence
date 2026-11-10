package com.leo.tienda.perfumes.model;

import jakarta.persistence.*;

@Entity
@Table(name = "imagen_perfume", uniqueConstraints = @UniqueConstraint(columnNames = {"perfume_id", "huella"}))
public class ImagenPerfume {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "perfume_id", nullable = false) private Perfumes perfume;
    @Lob @Column(nullable = false) private byte[] contenido;
    @Column(nullable = false, length = 20) private String tipo;
    @Column(nullable = false, length = 64) private String huella;
    @Column(nullable = false) private int orden;
    protected ImagenPerfume() {}
    public ImagenPerfume(Perfumes perfume, byte[] contenido, String tipo, String huella, int orden) {
        this.perfume = perfume; this.contenido = contenido; this.tipo = tipo; this.huella = huella; this.orden = orden;
    }
    public Long getId() { return id; }
    public byte[] getContenido() { return contenido; }
    public String getTipo() { return tipo; }
    public String getHuella() { return huella; }
    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }
}
