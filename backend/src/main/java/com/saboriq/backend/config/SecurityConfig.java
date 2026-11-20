package com.saboriq.backend.config;

import java.util.List;

import jakarta.servlet.DispatcherType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.cors(Customizer.withDefaults())
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .dispatcherTypeMatchers(
                                                                DispatcherType.ERROR,
                                                                DispatcherType.FORWARD)
                                                .permitAll()
                                                .requestMatchers(
                                                                "/api/health")
                                                .permitAll()
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/auth/registro",
                                                                "/api/auth/login")
                                                .permitAll()
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/categorias",
                                                                "/api/categorias/*")
                                                .permitAll()
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/categorias")
                                                .hasRole("ADMIN")
                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/categorias/*")
                                                .hasRole("ADMIN")
                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/categorias/*")
                                                .hasRole("ADMIN")
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/productos",
                                                                "/api/productos/**",
                                                                "/api/categorias",
                                                                "/api/categorias/**")
                                                .permitAll()
                                                .requestMatchers(
                                                                "/api/productos",
                                                                "/api/productos/**",
                                                                "/api/categorias",
                                                                "/api/categorias/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/marcas",
                                                                "/api/marcas/**")
                                                .permitAll()
                                                .requestMatchers(
                                                                "/api/marcas",
                                                                "/api/marcas/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/locales",
                                                                "/api/locales/**")
                                                .permitAll()
                                                .requestMatchers(
                                                                "/api/locales",
                                                                "/api/locales/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(
                                                                "/api/mesas",
                                                                "/api/mesas/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(
                                                                "/api/repartidores",
                                                                "/api/repartidores/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(
                                                                "/api/usuarios/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(
                                                                "/api/staff/**")
                                                .hasRole("STAFF")
                                                .requestMatchers(
                                                                "/api/reservas",
                                                                "/api/reservas/**")
                                                .hasRole("CLIENTE")
                                                .requestMatchers(
                                                                "/api/pagos",
                                                                "/api/pagos/**")
                                                .hasRole("CLIENTE")
                                                .requestMatchers(
                                                                "/api/reportes",
                                                                "/api/reportes/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(
                                                                "/api/pedidos",
                                                                "/api/pedidos/**")
                                                .hasRole("CLIENTE")
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt
                                                                .jwtAuthenticationConverter(
                                                                                jwtAuthenticationConverter())));

                return http.build();
        }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
                converter.setJwtGrantedAuthoritiesConverter(jwt -> {
                        String rol = jwt.getClaimAsString("rol");
                        if (!List.of("ADMIN", "CLIENTE", "STAFF").contains(rol)) {
                                return List.of();
                        }
                        return List.of(
                                        new SimpleGrantedAuthority("ROLE_" + rol));
                });
                return converter;
        }
}