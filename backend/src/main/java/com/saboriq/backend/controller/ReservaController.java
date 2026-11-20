package com.saboriq.backend.controller;

import com.saboriq.backend.dto.ReservaRequest;
import com.saboriq.backend.dto.ReservaResponse;
import com.saboriq.backend.service.ReservaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> crear(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReservaRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.crear(jwt.getSubject(), request));
    }

    @GetMapping("/mias")
    public ResponseEntity<List<ReservaResponse>> misReservas(
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(service.misReservas(jwt.getSubject()));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {

        return ResponseEntity.ok(service.cancelar(jwt.getSubject(), id));
    }
}