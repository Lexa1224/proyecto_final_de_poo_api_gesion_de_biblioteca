package com.ues.biblioteca.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "libro")
@Getter
@Setter
@NoArgsConstructor
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 20, unique = true)
    private String isbn;

    @Column(name = "anio_publicacion", nullable = false)
    private Integer anioPublicacion;

    @Column(nullable = false, length = 100)
    private String editorial;

    @Column(nullable = false)
    private Boolean disponible = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Autor autor;
}
