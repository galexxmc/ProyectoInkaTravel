package com.inkatravel.service;

import com.inkatravel.dto.ReservaRequestDTO;
import com.inkatravel.dto.ReservaResponseDTO;
import java.security.Principal;
import java.util.List; // <-- NUEVA IMPORTACIÓN

public interface ReservaService {

    /**
     * (RF-08) Crea una nueva reserva (PENDIENTE).
     */
    ReservaResponseDTO crearReserva(ReservaRequestDTO reservaDTO, Principal principal) throws Exception;

    /**
     * (NUEVO - RF-11) Obtiene el historial de reservas del usuario autenticado.
     * @param principal El usuario autenticado.
     * @return Una lista de sus reservas (en formato DTO).
     */
    List<ReservaResponseDTO> obtenerMisReservas(Principal principal) throws Exception;
}