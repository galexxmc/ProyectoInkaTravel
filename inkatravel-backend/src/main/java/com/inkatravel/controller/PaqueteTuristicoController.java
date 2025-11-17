package com.inkatravel.controller;

import com.fasterxml.jackson.databind.ObjectMapper; // NUEVO
import com.inkatravel.dto.PaqueteDetalleResponseDTO;
import com.inkatravel.dto.PaqueteTuristicoRequestDTO; // NUEVO
import com.inkatravel.dto.PaqueteTuristicoResponseDTO;
import com.inkatravel.model.PaqueteTuristico;
import com.inkatravel.service.PaqueteTuristicoService;
import com.inkatravel.service.StorageService; // NUEVO
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // NUEVO

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/paquetes")
@CrossOrigin(origins = "http://localhost:4200")
public class PaqueteTuristicoController {

    private final PaqueteTuristicoService paqueteService;
    private final StorageService storageService; // INYECCIÓN
    private final ObjectMapper objectMapper;     // INYECCIÓN

    // Constructor para Inyección de Dependencias
    public PaqueteTuristicoController(PaqueteTuristicoService paqueteService,
                                      StorageService storageService,
                                      ObjectMapper objectMapper) {
        this.paqueteService = paqueteService;
        this.storageService = storageService;
        this.objectMapper = objectMapper;
    }

    // --- MÉTODOS GET (Sin cambios en su lógica) ---

    @GetMapping
    public ResponseEntity<List<PaqueteTuristicoResponseDTO>> obtenerPaquetesFiltrados(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax
    ) {
        List<PaqueteTuristico> paquetes = paqueteService.obtenerPaquetesFiltrados(region, categoria, precioMin, precioMax);
        List<PaqueteTuristicoResponseDTO> paquetesDTO = paquetes.stream()
                .map(PaqueteTuristicoResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(paquetesDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPaquetePorId(@PathVariable Integer id) {
        try {
            PaqueteDetalleResponseDTO detalle = paqueteService.obtenerPaquetePorId(id);
            return ResponseEntity.ok(detalle);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // --- MÉTODO POST (Modificado para archivos) ---
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<PaqueteTuristicoResponseDTO> crearPaquete(
            @RequestPart("paquete") String paqueteDTOString,
            @RequestPart("imagen") MultipartFile imagenFile
    ) {
        try {
            // 1. Deserializar JSON (String) a DTO de Solicitud
            PaqueteTuristicoRequestDTO paqueteDTO = objectMapper.readValue(paqueteDTOString, PaqueteTuristicoRequestDTO.class);

            // 2. Guardar la imagen y obtener su nombre/ruta
            String nombreImagen = storageService.guardarArchivo(imagenFile);

            // 3. Asignar el nombre al DTO
            paqueteDTO.setImagenUrl(nombreImagen);

            // 4. Llamar al servicio
            PaqueteTuristicoResponseDTO nuevoPaqueteDTO = paqueteService.crearPaquete(paqueteDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPaqueteDTO);

        } catch (Exception e) {
            System.err.println("Error al crear paquete: " + e.getMessage()); // Log para depuración
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // --- MÉTODO PUT (Modificado para archivos) ---
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> actualizarPaquete(
            @PathVariable Integer id,
            @RequestPart("paquete") String paqueteDTOString,
            @RequestPart(value = "imagen", required = false) MultipartFile imagenFile // Imagen es opcional
    ) {
        try {
            // 1. Deserializar JSON (String) a DTO de Solicitud
            PaqueteTuristicoRequestDTO paqueteDetalles = objectMapper.readValue(paqueteDTOString, PaqueteTuristicoRequestDTO.class);
            paqueteDetalles.setId(id);

            // 2. Manejo de la imagen: Si viene un archivo, lo guardamos y asignamos la URL.
            if (imagenFile != null && !imagenFile.isEmpty()) {
                String nombreImagen = storageService.guardarArchivo(imagenFile);
                paqueteDetalles.setImagenUrl(nombreImagen);
            } else {
                // Si no viene archivo, el service mantendrá la URL existente.
                paqueteDetalles.setImagenUrl(null);
            }

            // 3. Llamar al servicio
            PaqueteTuristicoResponseDTO paqueteActualizadoDTO = paqueteService.actualizarPaquete(id, paqueteDetalles);
            return ResponseEntity.ok(paqueteActualizadoDTO);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // --- OTROS MÉTODOS (DELETE y Recomendados) ---

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPaquete(@PathVariable Integer id) {
        try {
            paqueteService.eliminarPaquete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

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