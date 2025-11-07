package com.inkatravel.repository;

import com.inkatravel.model.Pago;
import com.inkatravel.model.Reserva; // <-- IMPORTAR
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // <-- IMPORTAR

public interface PagoRepository extends JpaRepository<Pago, Integer> {

    /**
     * (NUEVO)
     * Busca un Pago usando el objeto Reserva.
     * Esto es necesario para la lógica de webhook (para evitar duplicados).
     */
    Optional<Pago> findByReserva(Reserva reserva);
}