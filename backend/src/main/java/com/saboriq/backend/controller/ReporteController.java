package com.saboriq.backend.controller;

import com.saboriq.backend.dto.ReporteMarcaResponse;
import com.saboriq.backend.service.ReporteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService service;

    public ReporteController(ReporteService service) {
        this.service = service;
    }

    @GetMapping("/reservas")
    public ResponseEntity<List<ReporteMarcaResponse>> obtenerReporte() {
        return ResponseEntity.ok(service.obtenerReporteConsolidado());
    }
}