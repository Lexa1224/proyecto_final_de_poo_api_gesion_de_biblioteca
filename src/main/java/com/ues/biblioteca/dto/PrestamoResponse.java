package com.ues.biblioteca.dto;

import java.time.LocalDate;

public record PrestamoResponse(
        Integer id,
        LocalDate fechaPrestamo,
        LocalDate fechaDevolucion,
        Boolean devuelto,
        Integer libroId,
        String libroTitulo,
        Integer socioId,
        String socioNombre
) {
}
