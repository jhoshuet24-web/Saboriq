package com.saboriq.backend.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record PedidoRequest(
        @NotNull Long localId,
        @NotBlank String direccionEntrega,
        @NotBlank String distritoEntrega,
        @NotEmpty List<DetallePedidoRequest> items) {
}