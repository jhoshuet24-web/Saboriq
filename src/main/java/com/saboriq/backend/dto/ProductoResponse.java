
package com.saboriq.backend.dto;

import java.math.BigDecimal;

public record ProductoResponse(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Boolean disponible,
        Long categoriaId,
        String categoriaNombre
) {}
