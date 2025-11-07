package com.inkatravel.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "punto_fidelidad")
public class PuntoFidelidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cantidad_puntos", nullable = false)
    private int cantidadPuntos; // Positivo para ganar, negativo para canjear

    private String motivo; // Ej: "Compra Paquete: Cusco", "Canje en Reserva #123"

    @Column(name = "fecha_otorgamiento", updatable = false)
    private LocalDateTime fechaOtorgamiento;

    @Column(name = "fecha_canje")
    private LocalDateTime fechaCanje; // Se llena solo si el 'motivo' es un canje

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @PrePersist
    protected void onCreate() {
        fechaOtorgamiento = LocalDateTime.now();
    }
}