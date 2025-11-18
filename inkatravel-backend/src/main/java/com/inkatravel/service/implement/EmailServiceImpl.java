package com.inkatravel.service.implement;

import com.inkatravel.service.EmailService;
import jakarta.mail.internet.MimeMessage; // <-- Importante para HTML
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper; // <-- Importante para HTML
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine; // <-- ¡NUEVO! Importar Thymeleaf
import org.thymeleaf.context.Context; // <-- ¡NUEVO! Importar Contexto
import java.math.BigDecimal; // <-- ¡NUEVO! Importar para el DTO

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine; // <-- ¡NUEVO! Inyectar Thymeleaf

    @Value("${spring.mail.username}")
    private String emailDesde;

    // --- ¡Constructor Actualizado! ---
    public EmailServiceImpl(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine; // <-- Inyectar Thymeleaf
    }

    /**
     * Método existente (para suscripción premium, etc.)
     */
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
            System.err.println("Error al enviar email simple: " + e.getMessage());
        }
    }

    /**
     * (NUEVO) Método para enviar la plantilla HTML de confirmación.
     */
    @Override
    public void enviarCorreoConfirmacion(
            String destinatarioEmail,
            String nombreUsuario,
            Integer reservaId,
            String paqueteNombre,
            BigDecimal totalPagado
    ) {

        try {
            // 1. Crear el Mensaje MIME (para HTML)
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            // Usamos MimeMessageHelper para HTML, adjuntos y UTF-8
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(destinatarioEmail);
            helper.setFrom(emailDesde);
            helper.setSubject("✅ ¡Tu reserva en InkaTravel está confirmada!");

            // 2. Crear el Contexto de Thymeleaf (las variables)
            Context context = new Context();
            context.setVariable("nombreUsuario", nombreUsuario);
            context.setVariable("reservaId", reservaId);
            context.setVariable("paqueteNombre", paqueteNombre);
            context.setVariable("totalPagado", totalPagado);

            // 3. Procesar la plantilla HTML (confirmation-email.html)
            String htmlBody = templateEngine.process("confirmation-email", context);

            // 4. Establecer el cuerpo del correo como HTML
            helper.setText(htmlBody, true); // El 'true' activa el modo HTML

            // 5. Enviar
            mailSender.send(mimeMessage);

        } catch (Exception e) {
            // Manejo de error
            System.err.println("Error al enviar correo HTML: " + e.getMessage());
        }
    }
}