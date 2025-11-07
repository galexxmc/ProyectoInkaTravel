package com.inkatravel.controller;

import com.inkatravel.dto.PaqueteTuristicoResponseDTO; // <-- IMPORTAR
import com.inkatravel.model.PaqueteTuristico;
import com.inkatravel.service.PaqueteTuristicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inkatravel.dto.PaqueteDetalleResponseDTO;

import org.springframework.web.bind.annotation.RequestParam; // <-- IMPORTAR
import java.math.BigDecimal; // <-- IMPORTAR

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors; // <-- IMPORTAR PARA LOS GET

@RestController
@RequestMapping("/api/paquetes")
@CrossOrigin(origins = "http://localhost:4200")
public class PaqueteTuristicoController {

    private final PaqueteTuristicoService paqueteService;

    public PaqueteTuristicoController(PaqueteTuristicoService paqueteService) {
        this.paqueteService = paqueteService;
    }

    /**
     * (ACTUALIZADO - RF-03 y RF-05)
     * Endpoint para obtener el catálogo, ahora con filtros.
     * Escuchará en: GET http://localhost:8080/api/paquetes
     * Y también en: GET http://localhost:8080/api/paquetes?region=Cusco
     * O en: GET http://localhost:8080/api/paquetes?precioMin=100&precioMax=500
     */
    @GetMapping
    public ResponseEntity<List<PaqueteTuristicoResponseDTO>> obtenerPaquetesFiltrados(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax
    ) {
        // 1. Llama al servicio con los filtros (que pueden ser 'null')
        List<PaqueteTuristico> paquetes = paqueteService.obtenerPaquetesFiltrados(region, categoria, precioMin, precioMax);

        // 2. Convierte la lista de Entidades a DTOs (para evitar el error 403)
        List<PaqueteTuristicoResponseDTO> paquetesDTO = paquetes.stream()
                .map(PaqueteTuristicoResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(paquetesDTO);
    }

    /**
     * (ACTUALIZADO - RF-04 y RF-14)
     * Endpoint para detalle de paquete (con clima).
     * Escuchará en: GET http://localhost:8080/api/paquetes/1
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPaquetePorId(@PathVariable Integer id) {
        try {
            // El servicio ahora devuelve el DTO contenedor
            PaqueteDetalleResponseDTO detalle = paqueteService.obtenerPaquetePorId(id);
            return ResponseEntity.ok(detalle);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * POST /api/paquetes (Actualizado para usar el servicio que devuelve DTO)
     */
    @PostMapping
    public ResponseEntity<PaqueteTuristicoResponseDTO> crearPaquete(@RequestBody PaqueteTuristico paquete) {
        // El servicio ya devuelve el DTO
        PaqueteTuristicoResponseDTO nuevoPaqueteDTO = paqueteService.crearPaquete(paquete);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPaqueteDTO);
    }

    /**
     * PUT /api/paquetes/{id} (Actualizado para usar el servicio que devuelve DTO)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPaquete(@PathVariable Integer id, @RequestBody PaqueteTuristico paqueteDetalles) {
        try {
            // El servicio ya devuelve el DTO
            PaqueteTuristicoResponseDTO paqueteActualizadoDTO = paqueteService.actualizarPaquete(id, paqueteDetalles);
            return ResponseEntity.ok(paqueteActualizadoDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * DELETE /api/paquetes/{id} (Este ya estaba bien)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPaquete(@PathVariable Integer id) {
        try {
            paqueteService.eliminarPaquete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * (NUEVO)
     * Endpoint para RF-13: Obtener recomendaciones personalizadas.
     * PROTEGIDO (Requiere JWT).
     * Escuchará en: GET http://localhost:8080/api/paquetes/recomendados
     */
    @GetMapping("/recomendados")
    public ResponseEntity<?> obtenerRecomendaciones(Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No estás autenticado.");
        }

        try {
            List<PaqueteTuristicoResponseDTO> recomendaciones = paqueteService.obtenerRecomendaciones(principal);
            return ResponseEntity.ok(recomendaciones);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}