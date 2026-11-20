package com.saboriq.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id, String localNombre, String direccionEntrega, String distritoEntrega,
        BigDecimal subtotal, BigDecimal costoEnvio, BigDecimal total, String estado,
        LocalDateTime creadoEn, List<DetallePedidoResponse> detalles,
        String repartidorNombre) {
}