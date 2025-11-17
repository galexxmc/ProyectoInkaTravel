// En: com.inkatravel.model/PaqueteTuristico.java
package com.inkatravel.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "paquete_turistico")
public class PaqueteTuristico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Lob
    private String descripcion;

    @Column(nullable = false)
    private BigDecimal precio;

    private String region;
    private String categoria;

    @Lob
    private String itinerario;

    private boolean disponibilidad = true;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitud;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitud;

    // --- NUEVO CAMPO ---
    @Column(name = "imagen_url")
    private String imagenUrl;
    // --- FIN NUEVO CAMPO ---

    @OneToMany(mappedBy = "paqueteTuristico")
    private Set<Reserva> reservas;
}