package com.inkatravel.repository;

import com.inkatravel.model.PaqueteTuristico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal; // <-- IMPORTAR
import java.util.List;

public interface PaqueteTuristicoRepository extends JpaRepository<PaqueteTuristico, Integer> {

    // (Estos ya los teníamos desde el Paso 4)
    List<PaqueteTuristico> findByRegion(String region);
    List<PaqueteTuristico> findByCategoria(String categoria);

    // --- (NUEVO) Métodos para RF-05 (Filtro de Precio) ---

    /**
     * Busca paquetes donde el precio sea MENOR O IGUAL a un máximo.
     */
    List<PaqueteTuristico> findByPrecioLessThanEqual(BigDecimal precioMax);

    /**
     * Busca paquetes donde el precio sea MAYOR O IGUAL a un mínimo.
     */
    List<PaqueteTuristico> findByPrecioGreaterThanEqual(BigDecimal precioMin);

    /**
     * Busca paquetes que estén DENTRO de un rango de precio.
     */
    List<PaqueteTuristico> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);

    /**
     * (NUEVO - RF-13)
     * Busca los 5 primeros paquetes de una región,
     * EXCLUYENDO el ID del paquete que el usuario ya compró.
     */
    List<PaqueteTuristico> findTop5ByRegionAndIdNot(String region, Integer id);
}