
package com.saboriq.backend.service;

import com.saboriq.backend.entity.Usuario;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final long expiration;
    private final String issuer;

    public JwtService(
            JwtEncoder encoder,
            @Value("${app.jwt.expiration}") long expiration,
            @Value("${app.jwt.issuer}") String issuer) {

        this.encoder = encoder;
        this.expiration = expiration;
        this.issuer = issuer;
    }

    public String generarToken(Usuario usuario) {

        Instant ahora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .audience(List.of("saboriq-api"))
                .issuedAt(ahora)
                .expiresAt(
                        ahora.plusSeconds(expiration)
                )
                .subject(usuario.getCorreo())
                .claim("rol", usuario.getRol().getNombre())
                .build();


        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        return encoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }
}
