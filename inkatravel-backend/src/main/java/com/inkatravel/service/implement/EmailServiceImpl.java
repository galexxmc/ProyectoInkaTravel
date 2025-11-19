package com.inkatravel.service.implement;

import com.inkatravel.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.math.BigDecimal;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    // Esta variable ahora tiene el ID técnico de Brevo,
    // así que NO la usaremos para el "From" visible.
    @Value("${spring.mail.username}")
    private String emailAuthUser;

    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void enviarEmailSimple(String para, String asunto, String texto) {
        try {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            // Forzamos el correo real aquí también
            mensaje.setFrom("inkatravelpeoficial@gmail.com");
            mensaje.setTo(para);
            mensaje.setSubject(asunto);
            mensaje.setText(texto);

            mailSender.send(mensaje);

        } catch (Exception e) {
            System.err.println("Error al enviar email simple: " + e.getMessage());
        }
    }

    @Override
    public void enviarCorreoConfirmacion(
            String destinatarioEmail,
            String nombreUsuario,
            Integer reservaId,
            String paqueteNombre,
            BigDecimal totalPagado
    ) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(destinatarioEmail);

            // --- ¡CAMBIO AQUÍ! ---
            // Usamos tu correo real y un nombre para mostrar ("InkaTravel PE")
            // El primer parámetro es el email, el segundo es el nombre que ve el cliente
            helper.setFrom("inkatravelpeoficial@gmail.com", "InkaTravel PE");
            // ---------------------

            helper.setSubject("✅ ¡Tu reserva en InkaTravel está confirmada!");

            Context context = new Context();
            context.setVariable("nombreUsuario", nombreUsuario);
            context.setVariable("reservaId", reservaId);
            context.setVariable("paqueteNombre", paqueteNombre);
            context.setVariable("totalPagado", totalPagado);

            String htmlBody = templateEngine.process("confirmation-email", context);

            helper.setText(htmlBody, true);

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            System.err.println("Error al enviar correo HTML: " + e.getMessage());
        }
    }
}