package com.inkatravel.controller;

import com.inkatravel.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Endpoint 2: Recibir el Webhook (Llamado por Mercado Pago)
     * PÚBLICO
     * Escuchará en: POST https://tu-url-de-ngrok.io/api/pagos/webhook
     */
    @PostMapping("/webhook")
    public ResponseEntity<?> recibirWebhook(@RequestBody Map<String, Object> payload) {
        try {
            // El SDK 2.1.7 a veces envía 'action' y 'data.id'
            // El SDK 2.5+ a veces envía 'type' y 'data.id'

            String topic = (String) payload.get("type"); // SDK 2.5+
            String action = (String) payload.get("action"); // SDK 2.1.7

            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            Long paymentId = null;

            if (data != null && data.get("id") != null) {
                paymentId = Long.parseLong(data.get("id").toString());
            }

            // Lógica de Webhook para SDK 2.1.7 (payment.created)
            if (action != null && action.equals("payment.created") && paymentId != null) {
                pagoService.procesarWebhook(paymentId, "payment");
            }
            // Lógica de Webhook para SDK 2.5+ (payment)
            else if (topic != null && topic.equals("payment") && paymentId != null) {
                pagoService.procesarWebhook(paymentId, "payment");
            }

            // Respondemos 200 OK para que Mercado Pago sepa que lo recibimos
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            // Si algo falla, MP lo reintentará.
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}