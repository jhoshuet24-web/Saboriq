package com.saboriq.backend.controller;

import com.saboriq.backend.dto.*;
import com.saboriq.backend.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearPedido(jwt.getSubject(), request));
    }

    @GetMapping("/mios")
    public ResponseEntity<List<PedidoResponse>> misPedidos(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.misPedidos(jwt.getSubject()));
    }
}