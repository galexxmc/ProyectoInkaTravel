// En: com.inkatravel.dto/PaqueteTuristicoRequestDTO.java
package com.inkatravel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PaqueteTuristicoRequestDTO {

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

    // --- CAMPO FALTANTE CORREGIDO ---
    // Necesario para que el Controller pase la URL al Service.
    private String imagenUrl;
    // --- FIN CAMPO CORREGIDO ---
}