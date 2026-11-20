package com.saboriq.backend.dto;

import java.math.BigDecimal;

public record DetallePedidoResponse(String productoNombre, Integer cantidad, BigDecimal precioUnitario) {
}