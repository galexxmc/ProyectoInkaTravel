package com.inkatravel.dto;

import com.inkatravel.dto.clima.OpenMeteoResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO Contenedor para la vista de "Detalle de Paquete".
 * Combina la info del paquete (RF-04) con la info del clima (RF-14).
 */
@Data
@AllArgsConstructor // Constructor simple
public class PaqueteDetalleResponseDTO {

    // Los datos de nuestra base de datos
    private PaqueteTuristicoResponseDTO paquete;

    // Los datos de la API externa
    private OpenMeteoResponseDTO clima;
}