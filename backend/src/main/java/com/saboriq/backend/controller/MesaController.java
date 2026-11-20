package com.saboriq.backend.controller;

import com.saboriq.backend.dto.MesaRequest;
import com.saboriq.backend.dto.MesaResponse;
import com.saboriq.backend.service.MesaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {

    private final MesaService service;

    public MesaController(MesaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MesaResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/local/{localId}")
    public ResponseEntity<List<MesaResponse>> listarPorLocal(
            @PathVariable Long localId) {

        return ResponseEntity.ok(service.listarPorLocal(localId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MesaResponse> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<MesaResponse> guardar(
            @Valid @RequestBody MesaRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.guardar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MesaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody MesaRequest request) {

        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}