
package com.saboriq.backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductoRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100)
        String nombre,

        @Size(max = 255)
        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01",
                message = "El precio debe ser mayor a cero")
        @Digits(integer = 8, fraction = 2)
        BigDecimal precio,

        @NotNull(message = "La disponibilidad es obligatoria")
        Boolean disponible,

        @NotNull(message = "La categoria es obligatoria")
        Long categoriaId

) {}
