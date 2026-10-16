
package com.saboriq.backend.controller;

import com.saboriq.backend.dto.RegistroRequest;
import com.saboriq.backend.dto.UsuarioResponse;
import com.saboriq.backend.service.AuthService;
import com.saboriq.backend.dto.LoginRequest;
import com.saboriq.backend.dto.LoginResponse;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(
            @Valid @RequestBody RegistroRequest request) {

        UsuarioResponse usuario = authService.registrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }


    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> miPerfil(
            @AuthenticationPrincipal Jwt jwt) {

        String correo = jwt.getSubject();

        return ResponseEntity.ok(
                authService.obtenerPerfil(correo)
        );
    }

}
