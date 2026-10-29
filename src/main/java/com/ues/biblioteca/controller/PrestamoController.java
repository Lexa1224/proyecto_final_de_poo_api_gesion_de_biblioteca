package com.ues.biblioteca.controller;

import com.ues.biblioteca.dto.PrestamoRequest;
import com.ues.biblioteca.dto.PrestamoResponse;
import com.ues.biblioteca.service.PrestamoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
@RequiredArgsConstructor
public class PrestamoController {

    private final PrestamoService prestamoService;

    @GetMapping
    public List<PrestamoResponse> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer socioId,
            @RequestParam(required = false) Integer libroId) {
        return prestamoService.listar(estado, socioId, libroId);
    }

    @GetMapping("/{id}")
    public PrestamoResponse obtener(@PathVariable Integer id) {
        return prestamoService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<PrestamoResponse> crear(@Valid @RequestBody PrestamoRequest request) {
        PrestamoResponse creado = prestamoService.crear(request);
        return ResponseEntity.created(URI.create("/api/prestamos/" + creado.id())).body(creado);
    }

    /**
     * Registra la devolucion. Si no se envia la fecha, se usa la fecha actual.
     * Ejemplo: PUT /api/prestamos/1/devolucion?fecha=2026-10-29
     */
    @PutMapping("/{id}/devolucion")
    public PrestamoResponse devolver(
            @PathVariable Integer id,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return prestamoService.devolver(id, fecha);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        prestamoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
