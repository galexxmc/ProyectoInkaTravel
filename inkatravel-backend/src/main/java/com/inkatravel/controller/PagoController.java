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

        // --- LOG DE DEPURACIÓN (Lo dejamos por si acaso) ---
        System.out.println("--- WEBHOOK RECIBIDO ---");
        System.out.println(payload);

        try {
            String topic = (String) payload.get("type");
            String action = (String) payload.get("action");

            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            Long paymentId = null;

            // --- ¡CAMBIO AQUÍ! ---
            // Si no hay 'data' o 'data.id', simplemente salimos con 200 OK
            // para que MP deje de insistir. No registramos un error.
            if (data == null || data.get("id") == null) {
                System.out.println("Webhook ignorado (sin data.id): " + topic);
                return ResponseEntity.ok().build(); // <-- Devolvemos OK
            }
            // --- FIN DEL CAMBIO ---

            paymentId = Long.parseLong(data.get("id").toString());

            // Lógica de Webhook para SDK 2.1.7 (payment.created)
            if (action != null && action.equals("payment.created") && paymentId != null) {
                pagoService.procesarWebhook(paymentId, "payment");
            }
            // Lógica de Webhook para SDK 2.5+ (payment)
            else if (topic != null && topic.equals("payment") && paymentId != null) {
                pagoService.procesarWebhook(paymentId, "payment");
            }

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            System.err.println("¡ERROR FATAL EN WEBHOOK! " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
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