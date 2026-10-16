
package com.saboriq.backend.dto;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {}
