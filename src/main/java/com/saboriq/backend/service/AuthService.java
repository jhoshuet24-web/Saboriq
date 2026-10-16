
package com.saboriq.backend.service;

import com.saboriq.backend.dto.RegistroRequest;
import com.saboriq.backend.dto.UsuarioResponse;
import com.saboriq.backend.entity.Rol;
import com.saboriq.backend.entity.Usuario;
import com.saboriq.backend.exception.RecursoDuplicadoException;
import com.saboriq.backend.exception.RecursoNoEncontradoException;
import com.saboriq.backend.repository.RolRepository;
import com.saboriq.backend.repository.UsuarioRepository;
import com.saboriq.backend.dto.LoginRequest;
import com.saboriq.backend.dto.LoginResponse;
import com.saboriq.backend.exception.CredencialesInvalidasException;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public UsuarioResponse registrar(RegistroRequest request) {

        String correo = request.correo()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (usuarioRepository.existsByCorreo(correo)) {
            throw new RecursoDuplicadoException(
                    "El correo ya se encuentra registrado"
            );
        }

        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "El rol CLIENTE no existe"
                        )
                );

        Usuario usuario = new Usuario();

        usuario.setNombre(request.nombre().trim());
        usuario.setCorreo(correo);

        usuario.setPassword(
                passwordEncoder.encode(request.password())
        );

        usuario.setRol(rolCliente);

        Usuario guardado = usuarioRepository.save(usuario);

        return new UsuarioResponse(
                guardado.getId(),
                guardado.getNombre(),
                guardado.getCorreo(),
                guardado.getRol().getNombre()
        );
    }


    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        String correo = request.correo()
                .trim()
                .toLowerCase(Locale.ROOT);

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(CredencialesInvalidasException::new);

        if (!passwordEncoder.matches(
                request.password(),
                usuario.getPassword())) {

            throw new CredencialesInvalidasException();
        }

        String token = jwtService.generarToken(usuario);

        return new LoginResponse(
                token,
                "Bearer",
                3600
        );
    }


    @Transactional(readOnly = true)
    public UsuarioResponse obtenerPerfil(String correo) {

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Usuario no encontrado"
                        )
                );

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().getNombre()
        );
    }

}
