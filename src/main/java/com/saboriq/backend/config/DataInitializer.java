package com.saboriq.backend.config;

import com.saboriq.backend.entity.Rol;
import com.saboriq.backend.entity.Usuario;
import com.saboriq.backend.repository.RolRepository;
import com.saboriq.backend.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;

@Configuration
public class DataInitializer {

    @Bean
    @Profile("dev")
    CommandLineRunner inicializarDatos(
            RolRepository rolRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap.admin.enabled:false}") boolean habilitado,
            @Value("${app.bootstrap.admin.email:}") String email,
            @Value("${app.bootstrap.admin.password:}") String password) {

        return args -> {

            Rol rolAdmin = rolRepository.findByNombre("ADMIN")
                    .orElseGet(() -> rolRepository.save(new Rol("ADMIN")));

            if (rolRepository.findByNombre("CLIENTE").isEmpty()) {
                rolRepository.save(new Rol("CLIENTE"));
            }

            if (!habilitado) {
                return;
            }

            String correo = email.trim().toLowerCase(Locale.ROOT);

            if (correo.isBlank() || password.isBlank()) {
                throw new IllegalStateException(
                        "Configura ADMIN_EMAIL y ADMIN_PASSWORD"
                );
            }

            if (usuarioRepository.existsByCorreo(correo)) {
                return;
            }

            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setCorreo(correo);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRol(rolAdmin);

            usuarioRepository.save(admin);

            System.out.println("Administrador inicial creado correctamente");
        };
    }
}
/**
package com.saboriq.backend.config;

import com.saboriq.backend.entity.Rol;
import com.saboriq.backend.repository.RolRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner inicializarRoles(
            RolRepository rolRepository) {

        return args -> {

            if (rolRepository.findByNombre("ADMIN").isEmpty()) {
                rolRepository.save(new Rol("ADMIN"));
            }

            if (rolRepository.findByNombre("CLIENTE").isEmpty()) {
                rolRepository.save(new Rol("CLIENTE"));
            }
        };
    }
}
*/