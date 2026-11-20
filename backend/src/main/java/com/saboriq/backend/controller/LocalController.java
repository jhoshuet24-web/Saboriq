package com.saboriq.backend.controller;

import com.saboriq.backend.dto.LocalRequest;
import com.saboriq.backend.dto.LocalResponse;
import com.saboriq.backend.service.LocalService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locales")
public class LocalController {

    private final LocalService service;

    public LocalController(LocalService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<LocalResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/marca/{marcaId}")
    public ResponseEntity<List<LocalResponse>> listarPorMarca(
            @PathVariable Long marcaId) {

        return ResponseEntity.ok(service.listarPorMarca(marcaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocalResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<LocalResponse> guardar(
            @Valid @RequestBody LocalRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.guardar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocalResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody LocalRequest request) {

        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}