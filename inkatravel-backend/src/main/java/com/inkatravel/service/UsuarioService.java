package com.inkatravel.service;

import com.inkatravel.dto.LoginRequestDTO;
import com.inkatravel.dto.LoginResponseDTO;
import com.inkatravel.dto.UsuarioResponseDTO;
import com.inkatravel.model.Usuario;

public interface UsuarioService {

    /**
     * Define la lógica para registrar un nuevo usuario (RF-01).
     * @param usuario El objeto Usuario con los datos del formulario
     * @return Un DTO seguro con los datos del usuario guardado
     */
    UsuarioResponseDTO registrarUsuario(Usuario usuario) throws Exception;

    /**
     * (NUEVO)
     * Define la lógica para el inicio de sesión (RF-02).
     * @param loginRequestDTO DTO con correo y contraseña
     * @return Un DTO con el Token JWT y los datos del usuario
     */
    LoginResponseDTO loginUsuario(LoginRequestDTO loginRequestDTO);

    // Aquí añadiremos más tarde:
    // Usuario hacerPremium(Integer usuarioId);
}