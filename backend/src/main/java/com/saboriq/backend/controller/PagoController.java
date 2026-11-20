package com.saboriq.backend.controller;

import com.saboriq.backend.dto.PagoRequest;
import com.saboriq.backend.dto.PagoResponse;
import com.saboriq.backend.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @PostMapping("/reserva/{reservaId}")
    public ResponseEntity<PagoResponse> pagarReserva(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long reservaId,
            @Valid @RequestBody PagoRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.pagarReserva(jwt.getSubject(), reservaId, request));
    }

    @PostMapping("/pedido/{pedidoId}")
    public ResponseEntity<PagoResponse> pagarPedido(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long pedidoId,
            @Valid @RequestBody PagoRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.pagarPedido(jwt.getSubject(), pedidoId, request));
    }
}