package com.inkatravel.dto;

import com.inkatravel.model.PuntoFidelidad;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO "plano" para el historial de puntos.
 * Evita la relación LAZY con Usuario.
 */
@Data
public class PuntoFidelidadResponseDTO {

    private Integer id;
    private int cantidadPuntos;
    private String motivo;
    private LocalDateTime fechaOtorgamiento;
    private LocalDateTime fechaCanje;
    private Integer usuarioId; // Solo el ID del usuario

    // Constructor que mapea la Entidad al DTO
    public PuntoFidelidadResponseDTO(PuntoFidelidad punto) {
        this.id = punto.getId();
        this.cantidadPuntos = punto.getCantidadPuntos();
        this.motivo = punto.getMotivo();
        this.fechaOtorgamiento = punto.getFechaOtorgamiento();
        this.fechaCanje = punto.getFechaCanje();
        this.usuarioId = punto.getUsuario().getId(); // Mapeo seguro
    }
}