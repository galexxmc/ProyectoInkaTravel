package com.inkatravel.service;

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
}