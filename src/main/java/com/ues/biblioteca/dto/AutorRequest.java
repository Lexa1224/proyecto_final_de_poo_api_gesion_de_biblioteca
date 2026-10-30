package com.ues.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AutorRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
        String nombre,

        @NotBlank(message = "La nacionalidad es obligatoria")
        @Size(max = 80, message = "La nacionalidad no puede superar los 80 caracteres")
        String nacionalidad
) {
}
