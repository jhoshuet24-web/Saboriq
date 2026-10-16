
package com.saboriq.backend.dto;

public record UsuarioResponse(
        Long id,
        String nombre,
        String correo,
        String rol
) {}
