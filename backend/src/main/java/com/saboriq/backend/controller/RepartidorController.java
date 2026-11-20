package com.saboriq.backend.controller;

import com.saboriq.backend.dto.RepartidorRequest;
import com.saboriq.backend.dto.RepartidorResponse;
import com.saboriq.backend.service.RepartidorService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repartidores")
public class RepartidorController {

    private final RepartidorService service;

    public RepartidorController(RepartidorService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RepartidorResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/local/{localId}")
    public ResponseEntity<List<RepartidorResponse>> listarPorLocal(
            @PathVariable Long localId) {

        return ResponseEntity.ok(service.listarPorLocal(localId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepartidorResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<RepartidorResponse> guardar(
            @Valid @RequestBody RepartidorRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.guardar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RepartidorResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RepartidorRequest request) {

        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}