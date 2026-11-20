package com.saboriq.backend.dto;

public record MarcaResponse(
                Long id,
                String nombre,
                String ruc,
                Boolean activa) {
}