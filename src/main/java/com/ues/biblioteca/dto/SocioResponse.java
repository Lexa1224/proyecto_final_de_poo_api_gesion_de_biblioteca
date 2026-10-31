package com.ues.biblioteca.dto;

public record SocioResponse(
        Integer id,
        String nombre,
        String numeroSocio,
        String email
) {
}
