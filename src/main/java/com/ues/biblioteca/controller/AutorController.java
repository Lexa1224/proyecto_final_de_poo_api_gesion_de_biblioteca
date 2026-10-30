package com.ues.biblioteca.controller;

import com.ues.biblioteca.dto.AutorRequest;
import com.ues.biblioteca.dto.AutorResponse;
import com.ues.biblioteca.service.AutorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/autores")
@RequiredArgsConstructor
public class AutorController {

    private final AutorService autorService;

    @GetMapping
    public List<AutorResponse> listar() {
        return autorService.listar();
    }

    @GetMapping("/{id}")
    public AutorResponse obtener(@PathVariable Integer id) {
        return autorService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<AutorResponse> crear(@Valid @RequestBody AutorRequest request) {
        AutorResponse creado = autorService.crear(request);
        return ResponseEntity.created(URI.create("/api/autores/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public AutorResponse actualizar(@PathVariable Integer id, @Valid @RequestBody AutorRequest request) {
        return autorService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        autorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
