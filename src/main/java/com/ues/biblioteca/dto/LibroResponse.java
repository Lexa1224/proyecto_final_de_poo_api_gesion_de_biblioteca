package com.ues.biblioteca.dto;

public record LibroResponse(
        Integer id,
        String titulo,
        String isbn,
        Integer anioPublicacion,
        String editorial,
        Boolean disponible,
        Integer autorId,
        String autorNombre
) {
}
