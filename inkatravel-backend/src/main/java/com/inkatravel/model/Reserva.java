package com.inkatravel.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "fecha_reserva", updatable = false)
    private LocalDateTime fechaReserva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado = EstadoReserva.PENDIENTE; // Valor por defecto

    @Column(name = "cantidad_viajeros", nullable = false)
    private int cantidadViajeros;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(name = "puntos_a_usar", nullable = false)
    private int puntosAUsar = 0;


    @ManyToOne(fetch = FetchType.LAZY) // LAZY es mejor para rendimiento
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paquete_id", nullable = false)
    private PaqueteTuristico paqueteTuristico;

    // CascadeType.ALL: si borro una reserva, se borra su pago asociado
    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Pago pago;

    @PrePersist
    protected void onCreate() {
        fechaReserva = LocalDateTime.now();
    }
}