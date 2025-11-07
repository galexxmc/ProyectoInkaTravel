package com.inkatravel.service;

import com.inkatravel.dto.PaqueteTuristicoResponseDTO; // <-- IMPORTAR
import com.inkatravel.model.PaqueteTuristico;

import java.security.Principal;
import java.util.List;

import com.inkatravel.dto.PaqueteDetalleResponseDTO;

import java.math.BigDecimal;

public interface PaqueteTuristicoService {

    // Cambia el retorno aquí
    PaqueteTuristicoResponseDTO crearPaquete(PaqueteTuristico paquete);
    /**
     * (ACTUALIZADO - RF-03 y RF-05)
     * Obtiene la lista de todos los paquetes, permitiendo filtros opcionales.
     * @param region Filtro opcional por región (ej: "Cusco")
     * @param categoria Filtro opcional por categoría (ej: "Aventura")
     * @param precioMin Filtro opcional de precio mínimo
     * @param precioMax Filtro opcional de precio máximo
     * @return Lista de paquetes filtrados.
     */
    // Dejamos este igual, pero lo cambiaremos en el controlador
    List<PaqueteTuristico> obtenerPaquetesFiltrados(String region, String categoria, BigDecimal precioMin, BigDecimal precioMax);

    // Dejamos este igual, pero lo cambiaremos en el controlador
    PaqueteDetalleResponseDTO obtenerPaquetePorId(Integer id) throws Exception;

    // Cambia el retorno aquí
    PaqueteTuristicoResponseDTO actualizarPaquete(Integer id, PaqueteTuristico paqueteDetalles) throws Exception;

    void eliminarPaquete(Integer id) throws Exception;

    /**
     * (NUEVO - RF-13)
     * Obtiene una lista de paquetes recomendados para el usuario logueado.
     * @param principal El usuario autenticado.
     * @return Lista de paquetes recomendados (en formato DTO).
     */
    List<PaqueteTuristicoResponseDTO> obtenerRecomendaciones(Principal principal) throws Exception;
}