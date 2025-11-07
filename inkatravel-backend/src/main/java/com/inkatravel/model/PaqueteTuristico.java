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

    @Lob // Para campos de texto largo (TEXT en MySQL)
    private String descripcion;

    @Column(nullable = false)
    private BigDecimal precio;

    private String region;
    private String categoria;

    @Lob
    private String itinerario;

    private boolean disponibilidad = true; // Valor por defecto

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(precision = 10, scale = 7) // Coincide con el DECIMAL(10, 7)
    private BigDecimal latitud;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitud;

    @OneToMany(mappedBy = "paqueteTuristico")
    private Set<Reserva> reservas;
}