package com.ues.biblioteca.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record PrestamoRequest(
        @NotNull(message = "El libro es obligatorio")
        Integer libroId,

        @NotNull(message = "El socio es obligatorio")
        Integer socioId,

        @PastOrPresent(message = "La fecha del prestamo no puede ser futura")
        LocalDate fechaPrestamo
) {
}
