package com.inkatravel.service.implement; // ¡Usando tu paquete personalizado!

import com.inkatravel.dto.LoginRequestDTO;
import com.inkatravel.dto.LoginResponseDTO;
import com.inkatravel.dto.UsuarioResponseDTO;
import com.inkatravel.model.TipoUsuario;
import com.inkatravel.model.Usuario;
import com.inkatravel.repository.UsuarioRepository;
import com.inkatravel.security.JwtService; // <-- NUEVA IMPORTACIÓN
import com.inkatravel.service.UsuarioService;
import org.springframework.security.authentication.AuthenticationManager; // <-- NUEVA IMPORTACIÓN
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // <-- NUEVA IMPORTACIÓN
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    // --- Dependencias ---
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; // <-- NUEVA DEPENDENCIA
    private final AuthenticationManager authenticationManager; // <-- NUEVA DEPENDENCIA

    // --- Inyección de Dependencias por Constructor ---
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder,
                              JwtService jwtService,
                              AuthenticationManager authenticationManager) { // <-- CONSTRUCTOR ACTUALIZADO
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Esta es la implementación REAL del RF-01
     * (ACTUALIZADO: Ahora devuelve un DTO seguro)
     */
    @Override
    public UsuarioResponseDTO registrarUsuario(Usuario usuario) throws Exception {

        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new Exception("El correo electrónico ya está registrado.");
        }

        String contrasenaPlana = usuario.getContrasena();
        String contrasenaEncriptada = passwordEncoder.encode(contrasenaPlana);
        usuario.setContrasena(contrasenaEncriptada);

        usuario.setTipo(TipoUsuario.GRATIS);
        usuario.setPuntosAcumulados(0);
        usuario.setSuscripcionActiva(false);

        // Guardamos el usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Devolvemos el DTO seguro, NO la entidad
        return new UsuarioResponseDTO(usuarioGuardado);
    }

    /**
     * (NUEVO)
     * Esta es la implementación REAL del RF-02 (Login)
     */
    @Override
    public LoginResponseDTO loginUsuario(LoginRequestDTO loginRequestDTO) {

        // 1. Autenticar al usuario
        // Esta línea usa el AuthenticationManager que configuramos.
        // Intenta autenticar al usuario usando el correo y la contraseña.
        // Si las credenciales son incorrectas, lanzará una excepción
        // (que será capturada por el controlador).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getCorreo(),
                        loginRequestDTO.getContrasena()
                )
        );

        // 2. Si la autenticación fue exitosa (no hubo excepción), buscar al usuario
        // Lo buscamos para poder pasárselo al generador de token.
        // Usamos orElseThrow por si acaso, aunque la línea anterior ya lo validó.
        Usuario usuario = usuarioRepository.findByCorreo(loginRequestDTO.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado después de la autenticación"));

        // 3. Generar el Token JWT
        String token = jwtService.generateToken(usuario); // (Usando el Usuario como UserDetails)

        // 4. Crear el DTO de respuesta seguro
        UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO(usuario);

        // 5. Devolver la respuesta completa
        return new LoginResponseDTO(token, usuarioDTO);
    }
}