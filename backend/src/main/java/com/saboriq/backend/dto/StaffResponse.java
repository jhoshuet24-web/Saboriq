package com.saboriq.backend.dto;

public record StaffResponse(
                Long id,
                String nombre,
                String correo,
                Long localId,
                String localNombre) {
}