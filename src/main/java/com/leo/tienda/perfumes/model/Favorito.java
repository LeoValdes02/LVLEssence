package com.leo.tienda.perfumes.model;

import jakarta.persistence.*;

@Entity
@Table(name = "favorito", uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "perfume_id"}))
public class Favorito {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "usuario_id", nullable = false) private Usuario usuario;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "perfume_id", nullable = false) private Perfumes perfume;
    protected Favorito() {}
    public Favorito(Usuario usuario, Perfumes perfume) { this.usuario = usuario; this.perfume = perfume; }
}
