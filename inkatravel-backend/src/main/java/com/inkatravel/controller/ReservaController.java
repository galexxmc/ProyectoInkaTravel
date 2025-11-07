package com.inkatravel.controller;

import com.inkatravel.dto.ReservaRequestDTO;
import com.inkatravel.model.Reserva;
import com.inkatravel.service.ReservaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.inkatravel.dto.ReservaResponseDTO;

import java.security.Principal; // ¡Importante para saber QUIÉN compra!

@RestController
@RequestMapping("/api/reservas") // URL base para las reservas
@CrossOrigin(origins = "http://localhost:4200")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    /**
     * Endpoint para RF-08: Crear una nueva reserva (Carrito de compras).
     * PROTEGIDO (Requiere JWT).
     * Escuchará en: POST http://localhost:8080/api/reservas
     */
    @PostMapping
    public ResponseEntity<?> crearNuevaReserva(
            @RequestBody ReservaRequestDTO reservaDTO,
            Principal principal // Spring inyecta al usuario autenticado (desde el JWT)
    ) {

        // Verificamos si el usuario está autenticado (aunque el filtro ya lo hizo)
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No estás autenticado.");
        }

        try {
            // Pasamos la petición Y la identidad del usuario al servicio
            ReservaResponseDTO nuevaReservaDTO = reservaService.crearReserva(reservaDTO, principal);

            // Devolvemos 201 Created y la reserva creada
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReservaDTO);

        } catch (Exception e) {
            // Si el servicio lanza un error (ej: "No tienes puntos", "Paquete no disponible")
            // Devolvemos 400 Bad Request con el mensaje claro.
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * (NUEVO)
     * Endpoint para RF-11: Ver "Mis Reservas".
     * PROTEGIDO (Requiere JWT).
     * Escuchará en: GET http://localhost:8080/api/reservas/mis-reservas
     */
    @GetMapping("/mis-reservas")
    public ResponseEntity<?> obtenerMisReservas(Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No estás autenticado.");
        }

        try {
            // Llama al servicio para obtener la lista de DTOs
            List<ReservaResponseDTO> misReservas = reservaService.obtenerMisReservas(principal);

            // Devuelve 200 OK y la lista de reservas
            return ResponseEntity.ok(misReservas);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}