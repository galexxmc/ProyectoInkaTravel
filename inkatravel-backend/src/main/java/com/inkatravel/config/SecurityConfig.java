package com.inkatravel.config;

import com.inkatravel.repository.UsuarioRepository;
import com.inkatravel.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;
    // private final JwtAuthFilter jwtAuthFilter; // <-- BORRA ESTA LÍNEA

    // Inyectamos SÓLO el Repositorio
    public SecurityConfig(UsuarioRepository usuarioRepository) { // <-- CONSTRUCTOR ACTUALIZADO
        this.usuarioRepository = usuarioRepository;
        // this.jwtAuthFilter = jwtAuthFilter; // <-- BORRA ESTA LÍNEA
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> usuarioRepository.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + username));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configuración final de la cadena de filtros de seguridad HTTP
     * (Aquí está la corrección)
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(authz -> authz
                        // --- Reglas Públicas ---
                        .requestMatchers("/api/usuarios/registro").permitAll()
                        .requestMatchers("/api/usuarios/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/paquetes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/paquetes/{id}").permitAll()
                        .requestMatchers("/api/pagos/webhook").permitAll() // <-- ¡AÑADIR ESTA LÍNEA!

                        // --- Reglas de ADMIN (RF-12) ---
                        .requestMatchers(HttpMethod.POST, "/api/paquetes").hasRole("ADMIN")
                        // ... (las otras reglas de admin)
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // --- Reglas de Usuario Logueado ---
                        .anyRequest().authenticated() // (Aquí entra /api/pagos/crear-checkout)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}