package com.inkatravel.service;

import com.inkatravel.dto.clima.OpenMeteoResponseDTO;
import java.math.BigDecimal;

/**
 * Define la lógica para conectarse a APIs externas de clima.
 */
public interface ClimaService {

    /**
     * Obtiene el clima actual para un par de coordenadas.
     * @param latitud La latitud.
     * @param longitud La longitud.
     * @return El DTO con la respuesta del clima.
     */
    OpenMeteoResponseDTO obtenerClimaActual(BigDecimal latitud, BigDecimal longitud);
}