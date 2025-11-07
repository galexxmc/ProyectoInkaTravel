package com.inkatravel.dto;

import com.inkatravel.model.PaqueteTuristico;
import lombok.Data;
import java.math.BigDecimal;

/**
 * DTO "plano" para devolver Paquetes Turísticos.
 * Evita el Set<Reserva> que causa el error LAZY.
 */
@Data
public class PaqueteTuristicoResponseDTO {

    private Integer id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String region;
    private String categoria;
    private String itinerario;
    private boolean disponibilidad;

    private BigDecimal latitud;
    private BigDecimal longitud;
    // No incluimos el Set<Reserva>

    // Constructor que mapea la Entidad al DTO
    public PaqueteTuristicoResponseDTO(PaqueteTuristico paquete) {
        this.id = paquete.getId();
        this.nombre = paquete.getNombre();
        this.descripcion = paquete.getDescripcion();
        this.precio = paquete.getPrecio();
        this.region = paquete.getRegion();
        this.categoria = paquete.getCategoria();
        this.itinerario = paquete.getItinerario();
        this.disponibilidad = paquete.isDisponibilidad();

        this.latitud = paquete.getLatitud();
        this.longitud = paquete.getLongitud();
    }
}