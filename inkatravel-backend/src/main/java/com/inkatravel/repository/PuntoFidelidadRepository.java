package com.inkatravel.repository;

import com.inkatravel.model.PuntoFidelidad;
import com.inkatravel.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PuntoFidelidadRepository extends JpaRepository<PuntoFidelidad, Integer> {

    // Para mostrar el historial de puntos en el Panel de Usuario (RF-11)
    List<PuntoFidelidad> findByUsuarioOrderByFechaOtorgamientoDesc(Usuario usuario);
}