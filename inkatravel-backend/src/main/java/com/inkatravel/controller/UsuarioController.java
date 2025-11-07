package com.inkatravel.controller;

import com.inkatravel.dto.LoginRequestDTO;
import com.inkatravel.dto.LoginResponseDTO;
import com.inkatravel.dto.UsuarioResponseDTO;
import com.inkatravel.model.Usuario;
import com.inkatravel.repository.UsuarioRepository; // <-- ¡NUEVA IMPORTACIÓN!
import com.inkatravel.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal; // <-- ¡NUEVA IMPORTACIÓN!

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository; // <-- ¡NUEVA DEPENDENCIA!

    // Actualiza el constructor para inyectar el repositorio
    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    // --- Endpoint de Registro (RF-01) ---
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            UsuarioResponseDTO usuarioGuardado = usuarioService.registrarUsuario(usuario);
            return ResponseEntity.ok(usuarioGuardado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- Endpoint de Login (RF-02) ---
    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            LoginResponseDTO response = usuarioService.loginUsuario(loginRequestDTO);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Credenciales incorrectas.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- (NUEVO) Endpoint de Prueba Protegido ---
    /**
     * Endpoint protegido para obtener el perfil del usuario logueado.
     * Escuchará en: GET http://localhost:8080/api/usuarios/perfil
     */
    @GetMapping("/perfil")
    public ResponseEntity<?> verMiPerfil(Principal principal) {
        // 'Principal' es un objeto de Spring Security que contiene
        // la información del usuario autenticado (gracias al JWT Filter).
        // principal.getName() nos devuelve el "username" (nuestro correo).

        // Buscamos al usuario en la BD usando el correo del token
        Usuario usuario = usuarioRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Devolvemos el DTO seguro
        return ResponseEntity.ok(new UsuarioResponseDTO(usuario));
    }
}