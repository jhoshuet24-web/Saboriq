package com.saboriq.backend.controller;

import com.saboriq.backend.dto.StaffRequest;
import com.saboriq.backend.dto.StaffResponse;
import com.saboriq.backend.dto.StaffUpdateRequest;
import com.saboriq.backend.service.StaffService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/staff")
public class StaffController {

    private final StaffService service;

    public StaffController(StaffService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<StaffResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    public ResponseEntity<StaffResponse> crear(
            @Valid @RequestBody StaffRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StaffResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody StaffUpdateRequest request) {

        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}