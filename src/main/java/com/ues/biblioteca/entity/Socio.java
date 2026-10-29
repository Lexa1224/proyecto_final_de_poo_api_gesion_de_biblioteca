package com.ues.biblioteca.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "socio")
@Getter
@Setter
@NoArgsConstructor
public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(name = "numero_socio", nullable = false, length = 20, unique = true)
    private String numeroSocio;

    @Column(nullable = false, length = 100, unique = true)
    private String email;
}
