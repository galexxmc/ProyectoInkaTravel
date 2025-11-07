package com.inkatravel.dto;

import com.inkatravel.model.EstadoReserva;
import com.inkatravel.model.Reserva;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO "plano" para devolver la respuesta de una reserva creada.
 * Esto evita errores de LazyInitializationException.
 */
@Data
public class ReservaResponseDTO {

    private Integer id;
    private LocalDateTime fechaReserva;
    private EstadoReserva estado;
    private int cantidadViajeros;
    private BigDecimal total;
    private int puntosAUsar;

    // Devolvemos solo los IDs, no los objetos completos
    private Integer usuarioId;
    private String usuarioNombre;
    private Integer paqueteId;
    private String paqueteNombre;

    // Constructor que mapea la Entidad al DTO
    public ReservaResponseDTO(Reserva reserva) {
        this.id = reserva.getId();
        this.fechaReserva = reserva.getFechaReserva();
        this.estado = reserva.getEstado();
        this.cantidadViajeros = reserva.getCantidadViajeros();
        this.total = reserva.getTotal();
        this.puntosAUsar = reserva.getPuntosAUsar();

        // Mapeo "seguro" de las relaciones lazy
        this.usuarioId = reserva.getUsuario().getId();
        this.usuarioNombre = reserva.getUsuario().getNombre();
        this.paqueteId = reserva.getPaqueteTuristico().getId();
        this.paqueteNombre = reserva.getPaqueteTuristico().getNombre();
    }
}