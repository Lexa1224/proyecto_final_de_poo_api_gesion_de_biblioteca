package com.ues.biblioteca.controller;

import com.ues.biblioteca.dto.SocioRequest;
import com.ues.biblioteca.dto.SocioResponse;
import com.ues.biblioteca.service.SocioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/socios")
@RequiredArgsConstructor
public class SocioController {

    private final SocioService socioService;

    @GetMapping
    public List<SocioResponse> listar() {
        return socioService.listar();
    }

    @GetMapping("/{id}")
    public SocioResponse obtener(@PathVariable Integer id) {
        return socioService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<SocioResponse> crear(@Valid @RequestBody SocioRequest request) {
        SocioResponse creado = socioService.crear(request);
        return ResponseEntity.created(URI.create("/api/socios/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public SocioResponse actualizar(@PathVariable Integer id, @Valid @RequestBody SocioRequest request) {
        return socioService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        socioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
