package com.ues.biblioteca.controller;

import com.ues.biblioteca.dto.LibroRequest;
import com.ues.biblioteca.dto.LibroResponse;
import com.ues.biblioteca.service.LibroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/libros")
@RequiredArgsConstructor
public class LibroController {

    private final LibroService libroService;

    @GetMapping
    public List<LibroResponse> listar(
            @RequestParam(required = false) Boolean disponible,
            @RequestParam(required = false) Integer autorId,
            @RequestParam(required = false) String titulo) {
        return libroService.listar(disponible, autorId, titulo);
    }

    @GetMapping("/{id}")
    public LibroResponse obtener(@PathVariable Integer id) {
        return libroService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<LibroResponse> crear(@Valid @RequestBody LibroRequest request) {
        LibroResponse creado = libroService.crear(request);
        return ResponseEntity.created(URI.create("/api/libros/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public LibroResponse actualizar(@PathVariable Integer id, @Valid @RequestBody LibroRequest request) {
        return libroService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        libroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
