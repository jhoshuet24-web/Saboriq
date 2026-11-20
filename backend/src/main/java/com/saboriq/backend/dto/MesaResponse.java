package com.saboriq.backend.dto;

public record MesaResponse(
                Long id,
                Long localId,
                String localNombre,
                Integer numero,
                Integer capacidad,
                Boolean activa) {
}