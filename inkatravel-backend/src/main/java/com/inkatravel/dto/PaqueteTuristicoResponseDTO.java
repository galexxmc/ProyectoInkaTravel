// En: com.inkatravel.dto/PaqueteTuristicoResponseDTO.java
package com.inkatravel.dto;

import com.inkatravel.model.PaqueteTuristico;
import lombok.Data;
import java.math.BigDecimal;

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

    // --- NUEVO CAMPO ---
    private String imagenUrl;
    // --- FIN NUEVO CAMPO ---

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

        // --- NUEVA LÍNEA EN EL CONSTRUCTOR ---
        this.imagenUrl = paquete.getImagenUrl();
        // --- FIN NUEVA LÍNEA ---
    }
}