package com.inkatravel.service.implement;

import com.inkatravel.model.PuntoFidelidad;
import com.inkatravel.model.Usuario;
import com.inkatravel.repository.PuntoFidelidadRepository;
import com.inkatravel.repository.UsuarioRepository;
import com.inkatravel.service.PuntoFidelidadService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors; // <-- IMPORTAR
import com.inkatravel.dto.PuntoFidelidadResponseDTO;

import java.security.Principal;
import java.util.List;

@Service
public class PuntoFidelidadServiceImpl implements PuntoFidelidadService {

    private final PuntoFidelidadRepository puntoFidelidadRepository;
    private final UsuarioRepository usuarioRepository;

    public PuntoFidelidadServiceImpl(PuntoFidelidadRepository puntoFidelidadRepository, UsuarioRepository usuarioRepository) {
        this.puntoFidelidadRepository = puntoFidelidadRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * (NUEVO) Implementación de RF-11
     */
    @Override
    @Transactional(readOnly = true)
    // Cambia el tipo de retorno aquí
    public List<PuntoFidelidadResponseDTO> obtenerMiHistorialDePuntos(Principal principal) throws Exception {

        Usuario usuario = usuarioRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        List<PuntoFidelidad> historial = puntoFidelidadRepository.findByUsuarioOrderByFechaOtorgamientoDesc(usuario);

        // --- ESTE ES EL CAMBIO ---
        // Convertimos la lista de Entidades a una lista de DTOs
        return historial.stream()
                .map(PuntoFidelidadResponseDTO::new) // Llama al constructor del DTO
                .collect(Collectors.toList());
    }
}