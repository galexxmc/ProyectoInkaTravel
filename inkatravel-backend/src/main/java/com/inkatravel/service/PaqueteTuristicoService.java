package com.inkatravel.service;

import com.inkatravel.dto.PaqueteTuristicoResponseDTO;
import com.inkatravel.dto.PaqueteTuristicoRequestDTO; // NUEVO IMPORTE
import com.inkatravel.model.PaqueteTuristico;

import java.security.Principal;
import java.util.List;

import com.inkatravel.dto.PaqueteDetalleResponseDTO;

import java.math.BigDecimal;

public interface PaqueteTuristicoService {

    // Ahora recibe el DTO de Solicitud (RequestDTO)
    PaqueteTuristicoResponseDTO crearPaquete(PaqueteTuristicoRequestDTO paqueteDTO);

    List<PaqueteTuristico> obtenerPaquetesFiltrados(String region, String categoria, BigDecimal precioMin, BigDecimal precioMax);

    PaqueteDetalleResponseDTO obtenerPaquetePorId(Integer id) throws Exception;

    // Ahora recibe el DTO de Solicitud (RequestDTO)
    PaqueteTuristicoResponseDTO actualizarPaquete(Integer id, PaqueteTuristicoRequestDTO paqueteDetalles);

    void eliminarPaquete(Integer id) throws Exception;

    List<PaqueteTuristicoResponseDTO> obtenerRecomendaciones(Principal principal) throws Exception;
}