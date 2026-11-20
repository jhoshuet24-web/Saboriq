package com.saboriq.backend.dto;

import java.math.BigDecimal;

public record ReporteMarcaResponse(
        String marcaNombre,
        Long totalReservas,
        Long reservasCompletadas,
        Long reservasNoShow,
        BigDecimal ingresosGarantias) {
}