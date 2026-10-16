
package com.saboriq.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Correo invalido")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        String password

) {}
