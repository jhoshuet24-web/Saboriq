package com.saboriq.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RepartidorRequest(
                @NotNull(message = "El local es obligatorio") Long localId,
                @NotBlank(message = "El nombre es obligatorio") @Size(max = 100, message = "El nombre no puede superar 100 caracteres") String nombre,
                @NotBlank(message = "El telefono es obligatorio") @Pattern(regexp = "^[0-9]{9}$", message = "El telefono debe tener 9 digitos") String telefono,
                @NotBlank(message = "El vehiculo es obligatorio") @Pattern(regexp = "MOTO|BICICLETA|AUTO", message = "El vehiculo debe ser MOTO, BICICLETA o AUTO") String vehiculo,
                @NotNull(message = "El estado es obligatorio") Boolean activo) {
}
