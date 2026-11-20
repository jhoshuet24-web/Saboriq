package com.saboriq.backend.dto;

import jakarta.validation.constraints.*;

public record DetallePedidoRequest(
        @NotNull Long productoId,
        @NotNull @Min(1) Integer cantidad) {
}