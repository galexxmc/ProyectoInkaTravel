package com.inkatravel.service;

import com.inkatravel.dto.PuntoFidelidadResponseDTO; // <-- IMPORTAR
import com.inkatravel.model.PuntoFidelidad;
import java.security.Principal;
import java.util.List;

public interface PuntoFidelidadService {

    // Cambia el tipo de retorno aquí
    List<PuntoFidelidadResponseDTO> obtenerMiHistorialDePuntos(Principal principal) throws Exception;
}