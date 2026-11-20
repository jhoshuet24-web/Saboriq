package com.saboriq.backend.dto;

public record RepartidorResponse(
                Long id,
                Long localId,
                String localNombre,
                String nombre,
                String telefono,
                String vehiculo,
                Boolean activo) {
}