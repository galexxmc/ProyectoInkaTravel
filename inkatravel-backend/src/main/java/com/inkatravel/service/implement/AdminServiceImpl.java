package com.inkatravel.service.implement;

import com.inkatravel.dto.ReservaResponseDTO;
import com.inkatravel.dto.UpdateRoleRequestDTO; // <-- IMPORTAR
import com.inkatravel.dto.UsuarioResponseDTO;
import com.inkatravel.model.Usuario; // <-- IMPORTAR
import com.inkatravel.repository.ReservaRepository;
import com.inkatravel.repository.UsuarioRepository;
import com.inkatravel.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.Principal; // <-- IMPORTAR


import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;

    public AdminServiceImpl(UsuarioRepository usuarioRepository, ReservaRepository reservaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> obtenerTodosLosUsuarios() {
        // 1. Busca todos los usuarios
        return usuarioRepository.findAll().stream()
                // 2. Convierte cada Usuario a UsuarioResponseDTO
                .map(UsuarioResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> obtenerTodasLasReservas() {
        // 1. Busca todas las reservas
        return reservaRepository.findAll().stream()
                // 2. Convierte cada Reserva a ReservaResponseDTO
                .map(ReservaResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizarRolUsuario(Integer id, UpdateRoleRequestDTO dto, Principal adminPrincipal) throws Exception {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // Regla de seguridad: Un admin no puede cambiarse el rol a sí mismo.
        if (usuario.getCorreo().equals(adminPrincipal.getName())) {
            throw new Exception("No puedes modificar tu propio rol.");
        }

        usuario.setTipo(dto.getNuevoRol()); // Asigna el nuevo rol
        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(usuarioActualizado);
    }

    @Override
    @Transactional
    public void desactivarUsuario(Integer id) throws Exception {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // (Podríamos añadir una regla para no banear Admins, pero por simpleza lo dejamos así)

        usuario.setActivo(false); // Lo "banea"
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void habilitarUsuario(Integer id) throws Exception {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        usuario.setActivo(true); // Lo "reactiva"
        usuarioRepository.save(usuario);
    }
}