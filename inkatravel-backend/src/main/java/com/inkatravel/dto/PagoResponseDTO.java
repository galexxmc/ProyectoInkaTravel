package com.inkatravel.dto;

import com.inkatravel.model.EstadoPago;
import com.inkatravel.model.Pago;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO "plano" para la respuesta de un Pago.
 * Evita la relación LAZY con Reserva.
 */
@Data
public class PagoResponseDTO {

    private Integer id;
    private String metodoPago;
    private BigDecimal monto;
    private LocalDateTime fechaPago;
    private EstadoPago estado;
    private String referenciaExterna;
    private Integer reservaId; // Solo el ID

    // Constructor que mapea la Entidad al DTO
    public PagoResponseDTO(Pago pago) {
        this.id = pago.getId();
        this.metodoPago = pago.getMetodoPago();
        this.monto = pago.getMonto();
        this.fechaPago = pago.getFechaPago();
        this.estado = pago.getEstado();
        this.referenciaExterna = pago.getReferenciaExterna();
        this.reservaId = pago.getReserva().getId(); // Mapeo seguro
    }
}