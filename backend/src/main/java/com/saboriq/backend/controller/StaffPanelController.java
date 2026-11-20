package com.saboriq.backend.controller;

import com.saboriq.backend.dto.MiLocalResponse;
import com.saboriq.backend.dto.PedidoResponse;
import com.saboriq.backend.dto.ReservaResponse;
import com.saboriq.backend.service.PedidoService;
import com.saboriq.backend.service.ReservaService;
import com.saboriq.backend.service.StaffService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffPanelController {

    private final StaffService service;
    private final ReservaService reservaService;
    private final PedidoService pedidoService;

    public StaffPanelController(StaffService service, ReservaService reservaService, PedidoService pedidoService) {
        this.service = service;
        this.reservaService = reservaService;
        this.pedidoService = pedidoService;
    }

    @GetMapping("/mi-local")
    public ResponseEntity<MiLocalResponse> miLocal(
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(service.miLocal(jwt.getSubject()));
    }

    @GetMapping("/reservas")
    public ResponseEntity<List<ReservaResponse>> listarReservas(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(reservaService.reservasDelLocal(jwt.getSubject()));
    }

    @PutMapping("/reservas/{id}/completar")
    public ResponseEntity<ReservaResponse> completarReserva(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        return ResponseEntity.ok(reservaService.cambiarEstadoStaff(jwt.getSubject(), id, "COMPLETADA"));
    }

    @PutMapping("/reservas/{id}/no-show")
    public ResponseEntity<ReservaResponse> marcarNoShow(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        return ResponseEntity.ok(reservaService.cambiarEstadoStaff(jwt.getSubject(), id, "NO_SHOW"));
    }

    @GetMapping("/pedidos")
    public ResponseEntity<List<PedidoResponse>> listarPedidos(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(pedidoService.pedidosDelLocal(jwt.getSubject()));
    }

    @PutMapping("/pedidos/{id}/estado")
    public ResponseEntity<PedidoResponse> cambiarEstadoPedido(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @RequestParam String estado,
            @RequestParam(required = false) Long repartidorId) {
        return ResponseEntity.ok(pedidoService.actualizarEstadoStaff(jwt.getSubject(), id, estado, repartidorId));
    }
}