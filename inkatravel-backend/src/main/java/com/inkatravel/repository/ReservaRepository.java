package com.inkatravel.repository;

import com.inkatravel.dto.MonthlySaleDTO;
import com.inkatravel.model.EstadoReserva; // <-- IMPORTAR
import com.inkatravel.model.Reserva;
import com.inkatravel.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; // <-- IMPORTAR

public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    // Esto lo usaremos para el PANEL DE USUARIO (RF-11)
    // para mostrar "Mis Reservas"
    List<Reserva> findByUsuario(Usuario usuario);

    /**
     * (NUEVO - RF-13)
     * Busca la reserva CONFIRMADA más reciente de un usuario.
     */
    Optional<Reserva> findFirstByUsuarioAndEstadoOrderByFechaReservaDesc(Usuario usuario, EstadoReserva estado);
    /**
     * (NUEVO) Verifica si existe al menos una reserva asociada a un ID de paquete.
     * Es más eficiente que contar (count) o traer la lista (find).
     */
    boolean existsByPaqueteTuristicoId(Integer paqueteId);

    // Suma el campo 'precioTotal' solo para reservas CONFIRMADAS
    // Asegúrate de que tu enum de EstadoReserva incluya CONFIRMADA
    @Query("SELECT SUM(r.total) FROM Reserva r WHERE r.estado = :status")
    BigDecimal sumTotalConfirmedSales(@Param("status") EstadoReserva status);
    // Cuenta reservas creadas después de una fecha específica
    long countByFechaReservaAfter(LocalDateTime fecha);

    /**
     * Obtiene la suma de ventas agrupadas por mes/año para un período.
     * Utiliza la interfaz de proyección MonthlySaleDTO.
     */
    @Query("SELECT " +
            "YEAR(r.fechaReserva) as year, " +
            "MONTH(r.fechaReserva) as month, " +
            "SUM(r.total) as total " +
            "FROM Reserva r " +
            "WHERE r.estado = :status AND r.fechaReserva >= :startDate " +
            // ¡SOLUCIÓN! Agrupar por las mismas funciones usadas en el SELECT
            "GROUP BY YEAR(r.fechaReserva), MONTH(r.fechaReserva) " +
            "ORDER BY year ASC, month ASC")
    List<MonthlySaleDTO> findMonthlySalesAfter(@Param("status") EstadoReserva status,
                                               @Param("startDate") LocalDateTime startDate);
}