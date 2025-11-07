package com.inkatravel.repository;

import com.inkatravel.model.EstadoReserva; // <-- IMPORTAR
import com.inkatravel.model.Reserva;
import com.inkatravel.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
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
}