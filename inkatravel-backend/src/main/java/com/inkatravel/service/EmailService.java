package com.inkatravel.service;

import java.math.BigDecimal;

/**
 * Define la lógica para enviar notificaciones (RF-15).
 */
public interface EmailService {

    /**
     * Envía un correo de texto simple.
     * @param para El email del destinatario (ej: ana@correo.com).
     * @param asunto El asunto del correo.
     * @param texto El cuerpo del mensaje.
     */
    void enviarEmailSimple(String para, String asunto, String texto);

    /**
     * (NUEVO) Envía el correo de confirmación de pago usando la plantilla HTML.
     */
    void enviarCorreoConfirmacion(
            String destinatarioEmail,
            String nombreUsuario,
            Integer reservaId,
            String paqueteNombre,
            BigDecimal totalPagado
    );
}