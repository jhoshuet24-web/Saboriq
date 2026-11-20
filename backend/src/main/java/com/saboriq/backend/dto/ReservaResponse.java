package com.saboriq.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservaResponse(
                Long id,
                Long localId,
                String localNombre,
                String marcaNombre,
                LocalDateTime fechaHora,
                Integer personas,
                String estado,
                Boolean requiereGarantia,
                BigDecimal montoGarantia,
                LocalDateTime expiraEn,
                LocalDateTime creadoEn,
                LocalDateTime canceladaEn,
                String garantiaResultado,
                String clienteNombre) {
}
