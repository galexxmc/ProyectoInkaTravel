package com.inkatravel.config;

import com.inkatravel.repository.UsuarioRepository;
import com.inkatravel.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // <-- Importar
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// --- ¡NUEVAS IMPORTACIONES PARA CORS! ---
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
// --- FIN DE NUEVAS IMPORTACIONES ---

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioRepository usuarioRepository;

    public SecurityConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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
     * (NUEVO) Configuración Global de CORS
     * Esto le dice al backend que confíe en el frontend de Angular.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite peticiones SÓLO desde tu frontend de Angular
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        // Permite los métodos HTTP que usamos
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Permite cabeceras importantes (como Authorization para el JWT)
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica esta regla a TODAS las rutas
        return source;
    }


    /**
     * (ACTUALIZADO) Cadena de filtros de seguridad HTTP
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {

        http
                // --- ¡NUEVO! Activa la configuración CORS de arriba ---
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(authz -> authz
                        // --- Reglas Públicas ---
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // <-- Permite TODOS los OPTIONS
                        .requestMatchers("/api/usuarios/registro").permitAll()
                        .requestMatchers("/api/usuarios/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/paquetes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/paquetes/{id}").permitAll()
                        .requestMatchers("/api/pagos/webhook").permitAll()
                        .requestMatchers("/api/pagos/webhook-suscripcion").permitAll()

                        // Hacemos públicas las URL de redirección de MP
                        .requestMatchers("/pago-exitoso").permitAll()
                        .requestMatchers("/pago-fallido").permitAll()
                        .requestMatchers("/pago-pendiente").permitAll()
                        .requestMatchers("/suscripcion-exitosa").permitAll()

                        // --- Reglas de ADMIN (RF-12) ---
                        .requestMatchers(HttpMethod.POST, "/api/paquetes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/paquetes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/paquetes/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // --- Reglas de Usuario Logueado ---
                        .anyRequest().authenticated()
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}