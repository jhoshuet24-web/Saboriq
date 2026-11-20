package com.saboriq.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record PagoRequest(
                @NotBlank(message = "El método de pago es obligatorio") String metodo) {
}