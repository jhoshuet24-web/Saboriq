package com.saboriq.backend.service;

import com.saboriq.backend.dto.ReporteMarcaResponse;
import com.saboriq.backend.repository.PagoRepository;
import com.saboriq.backend.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteService {

    private final ReservaRepository reservaRepository;
    private final PagoRepository pagoRepository;

    public ReporteService(ReservaRepository reservaRepository, PagoRepository pagoRepository) {
        this.reservaRepository = reservaRepository;
        this.pagoRepository = pagoRepository;
    }

    @Transactional(readOnly = true)
    public List<ReporteMarcaResponse> obtenerReporteConsolidado() {

        Map<String, BigDecimal> ingresosMap = new HashMap<>();
        List<Object[]> ingresosData = pagoRepository.obtenerIngresosPorMarca();
        for (Object[] row : ingresosData) {
            String marca = (String) row[0];
            BigDecimal total = (BigDecimal) row[1];
            ingresosMap.put(marca, total);
        }

        List<ReporteMarcaResponse> reporte = new ArrayList<>();
        List<Object[]> reservasData = reservaRepository.obtenerResumenReservasPorMarca();

        for (Object[] row : reservasData) {
            String marca = (String) row[0];
            Long totalReservas = (Long) row[1];
            Long completadas = (Long) row[2];
            Long noShow = (Long) row[3];

            BigDecimal ingresos = ingresosMap.getOrDefault(marca, BigDecimal.ZERO);

            reporte.add(new ReporteMarcaResponse(
                    marca,
                    totalReservas,
                    completadas,
                    noShow,
                    ingresos));
        }

        return reporte;
    }
}