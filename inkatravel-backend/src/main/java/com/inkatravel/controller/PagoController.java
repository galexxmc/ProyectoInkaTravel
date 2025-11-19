package com.inkatravel.controller;

import com.inkatravel.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*") // Permitimos todo
public class PagoController {

    @Autowired
    private PagoService pagoService;

    /**
     * Endpoint 1: Crear el Link de Pago (Llamado por Angular)
     * PROTEGIDO (Requiere JWT)
     * Escuchará en: POST http://localhost:8080/api/pagos/crear-checkout/5
     */
    @PostMapping("/crear-checkout/{reservaId}")
    public ResponseEntity<?> crearLinkDePago(@PathVariable Integer reservaId) {
        try {
            String linkDePago = pagoService.crearLinkDePago(reservaId);
            // Devolvemos un JSON simple con la URL
            return ResponseEntity.ok(Map.of("url", linkDePago));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> recibirWebhook(@RequestBody Map<String, Object> payload) {

        System.out.println("--- WEBHOOK RECIBIDO (Payload Raw) ---");
        System.out.println(payload);

        try {
            // 1. Extraer data
            Map<String, Object> data = (Map<String, Object>) payload.get("data");

            // 2. Validación simple: ¿Tiene un ID?
            if (data == null || data.get("id") == null) {
                // Si no hay ID, es una notificación que no nos sirve (ej: merchant_order sin pago)
                return ResponseEntity.ok().build();
            }

            // 3. Obtener el ID del Pago
            Long paymentId = Long.parseLong(data.get("id").toString());
            String type = (String) payload.get("type");
            String action = (String) payload.get("action");

            System.out.println("Procesando Payment ID: " + paymentId + " | Action: " + action + " | Type: " + type);

            // 4. ¡CAMBIO CRUCIAL!
            // Procesamos SIEMPRE que haya un paymentId.
            // Eliminamos el filtro estricto de "payment.created".
            // Esto permite que pasen los eventos "payment.updated" (que traen la aprobación).
            pagoService.procesarWebhook(paymentId, "payment");

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            System.err.println("Error procesando webhook: " + e.getMessage());
            // Respondemos OK para que Mercado Pago no siga reintentando si es un error nuestro de lógica
            return ResponseEntity.ok().build();
        }
    }

    /**
     * (NUEVO - RF-06 Simulado)
     * Endpoint 3: Crear Link de Pago para Suscripción (Llamado por Angular)
     * PROTEGIDO (Requiere JWT)
     */
    @PostMapping("/crear-checkout-premium")
    public ResponseEntity<?> crearCheckoutPremium(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("No estás autenticado.");
        }
        try {
            // Llama al nuevo método del servicio
            String linkDePago = pagoService.crearLinkSuscripcion(principal);
            return ResponseEntity.ok(Map.of("url", linkDePago));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}