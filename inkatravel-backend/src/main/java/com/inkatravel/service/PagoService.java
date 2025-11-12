package com.inkatravel.service;

import com.inkatravel.dto.PagoResponseDTO; // <-- IMPORTAR
import java.security.Principal;
/**
 * Lógica de negocio para Pagos y Confirmaciones.
 */
public interface PagoService {

    /**
     * (Flujo Manual Admin)
     * Confirma un pago manualmente.
     */
    PagoResponseDTO confirmarPagoAdmin(Integer reservaId, String metodoPagoAdmin) throws Exception;

    /**
     * (NUEVO - Flujo 2)
     * Llama al SDK de Mercado Pago para crear una preferencia de pago.
     * @param reservaId El ID de la reserva PENDIENTE.
     * @return El link (URL) al checkout de Mercado Pago.
     */
    String crearLinkDePago(Integer reservaId) throws Exception;

    /**
     * (NUEVO - Flujo 2)
     * Procesa la notificación (webhook) de Mercado Pago.
     * @param paymentId El ID del pago (ej: 123456789).
     * @param topic El tipo de notificación (ej: "payment").
     */
    void procesarWebhook(Long paymentId, String topic) throws Exception;
    /**
     * (NUEVO - RF-06 Simulado)
     * Crea un link de pago ÚNICO para la suscripción Premium.
     * @param principal El usuario que se está suscribiendo.
     * @return El link (URL) al checkout de Mercado Pago.
     */
    String crearLinkSuscripcion(Principal principal) throws Exception;

}