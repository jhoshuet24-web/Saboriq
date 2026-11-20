package com.saboriq.backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record LocalRequest(
                @NotNull(message = "La marca es obligatoria") Long marcaId,
                @NotBlank(message = "El nombre es obligatorio") @Size(max = 100, message = "El nombre no puede superar 100 caracteres") String nombre,
                @NotBlank(message = "La direccion es obligatoria") @Size(max = 200, message = "La direccion no puede superar 200 caracteres") String direccion,
                @NotBlank(message = "El distrito es obligatorio") @Size(max = 80, message = "El distrito no puede superar 80 caracteres") String distrito,
                @NotBlank(message = "La ciudad es obligatoria") @Size(max = 80, message = "La ciudad no puede superar 80 caracteres") String ciudad,
                @Size(max = 20, message = "El telefono no puede superar 20 caracteres") String telefono,
                @NotNull(message = "El aforo es obligatorio") @Min(value = 1, message = "El aforo debe ser mayor a cero") Integer aforo,
                @NotNull(message = "El pedido minimo es obligatorio") @DecimalMin(value = "0.00", message = "El pedido minimo no puede ser negativo") @Digits(integer = 8, fraction = 2) BigDecimal pedidoMinimo,
                @NotNull(message = "El costo de envio es obligatorio") @DecimalMin(value = "0.00", message = "El costo de envio no puede ser negativo") @Digits(integer = 8, fraction = 2) BigDecimal costoEnvio,
                @NotNull(message = "El estado es obligatorio") Boolean activo,
                @Size(max = 500, message = "La cobertura no puede superar 500 caracteres") String distritosCobertura) {
}