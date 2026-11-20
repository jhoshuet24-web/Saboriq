package com.saboriq.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MesaRequest(
                @NotNull(message = "El local es obligatorio") Long localId,
                @NotNull(message = "El numero de mesa es obligatorio") @Min(value = 1, message = "El numero de mesa debe ser mayor a cero") Integer numero,
                @NotNull(message = "La capacidad es obligatoria") @Min(value = 1, message = "La capacidad debe ser mayor a cero") Integer capacidad,
                @NotNull(message = "El estado es obligatorio") Boolean activa

) {
}