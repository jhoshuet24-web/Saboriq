package com.saboriq.backend.dto;

import java.math.BigDecimal;

public record LocalResponse(
                Long id,
                Long marcaId,
                String marcaNombre,
                String nombre,
                String direccion,
                String distrito,
                String ciudad,
                String telefono,
                Integer aforo,
                BigDecimal pedidoMinimo,
                BigDecimal costoEnvio,
                Boolean activo,
                String distritosCobertura) {
}