package com.saboriq.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(
        Long id,
        Long reservaId,
        BigDecimal monto,
        String metodo,
        String estado,
        String referencia,
        LocalDateTime creadoEn,
        Long pedidoId) {
}