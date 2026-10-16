
package com.saboriq.backend.dto;

import jakarta.validation.constraints.NotNull;

public record DisponibilidadRequest(
        @NotNull Boolean disponible
) {}
