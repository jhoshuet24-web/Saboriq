package com.saboriq.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MarcaRequest(

                @NotBlank(message = "El nombre es obligatorio") @Size(max = 100, message = "El nombre no puede superar 100 caracteres") String nombre,

                @Pattern(regexp = "^[0-9]{11}$", message = "El RUC debe tener 11 digitos") String ruc,

                @NotNull(message = "El estado es obligatorio") Boolean activa

) {
}