package com.inkatravel.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull; // Importante para las anotaciones
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // ¡Importante!
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter; // Clase clave

import java.io.IOException;

@Component // Le dice a Spring que esto es un componente y que debe gestionarlo
public class JwtAuthFilter extends OncePerRequestFilter { // Se asegura de ejecutarse 1 vez por petición

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // El Bean que creamos en SecurityConfig

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Este es el método "portero" que se ejecuta en cada petición.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extraer la cabecera "Authorization"
        final String authHeader = request.getHeader("Authorization");

        // 2. Validar si la cabecera es nula o no empieza con "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Si no hay token, sigue al siguiente filtro
            return; // Y termina la ejecución aquí
        }

        // 3. Si la cabecera existe, extraer el token (quitando "Bearer ")
        final String token = authHeader.substring(7); // "Bearer ".length() == 7
        final String userEmail; // El correo del usuario

        try {
            // 4. Extraer el "username" (correo) del token
            userEmail = jwtService.extractUsername(token);
        } catch (Exception e) {
            // Si el token está malformado o expirado, simplemente no autenticamos
            // (La respuesta de error la dará Spring Security más adelante si la ruta lo requiere)
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Validar que el correo no sea nulo Y que el usuario no esté YA autenticado
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Cargar el usuario (UserDetails) desde la BD usando el correo
            // (Esto usa el Bean 'userDetailsService' que definimos en SecurityConfig)
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 7. Validar si el token es correcto (compara el usuario y la expiración)
            if (jwtService.isTokenValid(token, userDetails)) {

                // 8. ¡El token es válido! Creamos la autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, // El usuario autenticado
                        null,        // No usamos credenciales (password) aquí
                        userDetails.getAuthorities() // Los roles/permisos (ej: "PREMIUM")
                );

                // 9. Añadimos detalles extra (como la IP) a la autenticación
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 10. ESTABLECEMOS LA AUTENTICACIÓN en el contexto de seguridad
                // ¡Spring Security ahora sabe que este usuario está logueado!
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 11. Continuar con el resto de filtros (hacia el controlador)
        filterChain.doFilter(request, response);
    }
}