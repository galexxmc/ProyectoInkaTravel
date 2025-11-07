package com.inkatravel.service.implement;

import com.inkatravel.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    // Spring inyecta el sender configurado en application.properties
    private final JavaMailSender mailSender;

    // Inyectamos el email "de" (el que configuramos)
    @Value("${spring.mail.username}")
    private String emailDesde;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarEmailSimple(String para, String asunto, String texto) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(emailDesde);
            mensaje.setTo(para);
            mensaje.setSubject(asunto);
            mensaje.setText(texto);

            mailSender.send(mensaje);

        } catch (Exception e) {
            // En un proyecto real, esto debería ser un log más robusto
            System.err.println("Error al enviar email: " + e.getMessage());
        }
    }
}