package com.inkatravel.controller;

import com.inkatravel.model.PuntoFidelidad;
import com.inkatravel.service.PuntoFidelidadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inkatravel.dto.PuntoFidelidadResponseDTO;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/puntos") // URL base para los puntos
@CrossOrigin(origins = "http://localhost:4200")
public class PuntoFidelidadController {

    private final PuntoFidelidadService puntoFidelidadService;

    public PuntoFidelidadController(PuntoFidelidadService puntoFidelidadService) {
        this.puntoFidelidadService = puntoFidelidadService;
    }

    /**
     * Endpoint para RF-11: Ver "Mi Historial de Puntos".
     * PROTEGIDO (Requiere JWT).
     * Escuchará en: GET http://localhost:8080/api/puntos/historial
     */
    @GetMapping("/historial")
    public ResponseEntity<?> obtenerMiHistorial(Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No estás autenticado.");
        }

        try {
            // Esta variable ahora es de tipo DTO
            List<PuntoFidelidadResponseDTO> historial = puntoFidelidadService.obtenerMiHistorialDePuntos(principal);

            // Devolvemos la lista de DTOs (que es segura)
            return ResponseEntity.ok(historial);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}