package com.leo.tienda.perfumes.model;
import jakarta.persistence.*;
@Entity @Table(name="valoracion", uniqueConstraints=@UniqueConstraint(columnNames={"usuario_id","perfume_id"}))
public class Valoracion {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="usuario_id", nullable=false) private Usuario usuario;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="perfume_id", nullable=false) private Perfumes perfume;
    @Column(nullable=false) private int puntuacion;
    protected Valoracion() {}
    public Valoracion(Usuario u, Perfumes p, int n) { usuario=u; perfume=p; puntuacion=n; }
    public void setPuntuacion(int n) { puntuacion=n; }
}
