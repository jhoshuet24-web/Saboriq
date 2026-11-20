package com.saboriq.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservaRequest(

                @NotNull(message = "El local es obligatorio") Long localId,

                @NotNull(message = "La fecha y hora son obligatorias") LocalDateTime fechaHora,

                @NotNull(message = "El numero de personas es obligatorio") @Min(value = 1, message = "Debe haber al menos 1 persona") @Max(value = 50, message = "El maximo es de 50 personas por reserva") Integer personas

) {
}