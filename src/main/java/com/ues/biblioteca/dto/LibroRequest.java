package com.ues.biblioteca.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LibroRequest(
        @NotBlank(message = "El titulo es obligatorio")
        @Size(max = 150, message = "El titulo no puede superar los 150 caracteres")
        String titulo,

        @NotBlank(message = "El ISBN es obligatorio")
        @Size(max = 20, message = "El ISBN no puede superar los 20 caracteres")
        String isbn,

        @NotNull(message = "El anio de publicacion es obligatorio")
        @Min(value = 1000, message = "El anio de publicacion no es valido")
        @Max(value = 2100, message = "El anio de publicacion no es valido")
        Integer anioPublicacion,

        @NotBlank(message = "La editorial es obligatoria")
        @Size(max = 100, message = "La editorial no puede superar los 100 caracteres")
        String editorial,

        @NotNull(message = "El autor es obligatorio")
        Integer autorId
) {
}
