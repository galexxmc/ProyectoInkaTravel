package com.inkatravel.service.implement;

import com.inkatravel.dto.PagoResponseDTO;
import com.inkatravel.model.*;
import com.inkatravel.repository.*;
import com.inkatravel.service.EmailService;
import com.inkatravel.service.PagoService;
import org.springframework.beans.factory.annotation.Value; // <-- IMPORTAR
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

// --- SDK de Mercado Pago ---
import com.mercadopago.client.payment.PaymentClient; // <-- IMPORTAR
import com.mercadopago.client.preference.*; // <-- IMPORTAR
import com.mercadopago.resources.payment.Payment; // <-- IMPORTAR
import com.mercadopago.resources.preference.Preference; // <-- IMPORTAR
import java.util.List; // <-- IMPORTAR
import java.util.Map; // <-- IMPORTAR (para el SDK 2.1.7)


@Service
public class PagoServiceImpl implements PagoService {

    // --- Repositorios y Servicios ---
    private final ReservaRepository reservaRepository;
    private final PagoRepository pagoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PuntoFidelidadRepository puntoFidelidadRepository;
    private final EmailService emailService;

    // --- Constantes de Puntos ---
    private static final BigDecimal FACTOR_PUNTOS_GRATIS = new BigDecimal("10");
    private static final BigDecimal FACTOR_PUNTOS_PREMIUM = new BigDecimal("5");

    // --- Variables de application.properties ---
    @Value("${app.base-url}")
    private String appBaseUrl; // Tu URL de ngrok

    // Constructor
    public PagoServiceImpl(ReservaRepository reservaRepository, PagoRepository pagoRepository,
                           UsuarioRepository usuarioRepository, PuntoFidelidadRepository puntoFidelidadRepository,
                           EmailService emailService) {
        this.reservaRepository = reservaRepository;
        this.pagoRepository = pagoRepository;
        this.usuarioRepository = usuarioRepository;
        this.puntoFidelidadRepository = puntoFidelidadRepository;
        this.emailService = emailService;
    }

    /**
     * (Flujo 1) Endpoint del Admin (Manual)
     */
    @Override
    @Transactional
    public PagoResponseDTO confirmarPagoAdmin(Integer reservaId, String metodoPagoAdmin) throws Exception {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new Exception("Reserva no encontrada"));

        // Llama a la lógica central
        return confirmarLogicaDePago(reserva, metodoPagoAdmin, null);
    }

    /**
     * (Flujo 2) Endpoint 1: Crear el Link de Pago
     */
    @Override
    public String crearLinkDePago(Integer reservaId) throws Exception {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new Exception("Reserva no encontrada"));

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new Exception("Esta reserva ya no está pendiente.");
        }

        // 1. Crear el ítem de pago
        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .id(reserva.getPaqueteTuristico().getId().toString())
                .title(reserva.getPaqueteTuristico().getNombre())
                .description("Reserva para " + reserva.getCantidadViajeros() + " viajero(s)")
                .quantity(1) // Siempre 1, el total ya está calculado
                .currencyId("PEN") // Soles Peruanos
                .unitPrice(reserva.getTotal()) // El total NETO (con descuento)
                .build();

        // 2. Definir las URLs de redirección
        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(appBaseUrl + "/pago-exitoso") // Angular recibirá esto
                .failure(appBaseUrl + "/pago-fallido")
                .pending(appBaseUrl + "/pago-pendiente")
                .build();

        // 3. Crear la Preferencia de Pago
        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(List.of(itemRequest))
                .backUrls(backUrls)
                .autoReturn("approved") // Redirige automáticamente al aprobar
                .notificationUrl(appBaseUrl + "/api/pagos/webhook") // ¡LA CLAVE!
                .externalReference(reserva.getId().toString()) // Vincula el pago a nuestra reserva
                .build();

        // 4. Llamar al SDK de Mercado Pago
        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        // 5. Devolver el link de pago (URL del checkout)
        return preference.getInitPoint();
    }

    /**
     * (Flujo 2) Endpoint 2: Procesar el Webhook
     */
    @Override
    @Transactional
    public void procesarWebhook(Long paymentId, String topic) throws Exception {
        if (topic == null || (!topic.equals("payment") && !topic.equals("merchant_order"))) {
            return; // Ignoramos notificaciones que no sean de pago
        }

        // 1. Consultar a Mercado Pago por los detalles de este pago
        PaymentClient client = new PaymentClient();
        Payment payment = client.get(paymentId);

        // 2. Verificar que el pago esté APROBADO
        if (payment.getStatus() == null || !payment.getStatus().equals("approved")) {
            return; // El pago no fue aprobado (falló, pendiente, etc.)
        }

        // 3. Obtener el ID de nuestra reserva (que guardamos en 'external_reference')
        Integer reservaId = Integer.parseInt(payment.getExternalReference());

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new Exception("Webhook: Reserva no encontrada con ID: " + reservaId));

        // 4. Llamar a la lógica central
        confirmarLogicaDePago(reserva, "Mercado Pago (Automático)", paymentId.toString());
    }


    /**
     * LÓGICA CENTRAL REUTILIZABLE
     * (Confirmar BD, Dar Puntos, Enviar Email)
     */
    private PagoResponseDTO confirmarLogicaDePago(Reserva reserva, String metodoPago, String referenciaExterna) throws Exception {

        if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
            // Así manejas el Optional
            Pago pagoExistente = pagoRepository.findByReserva(reserva)
                    .orElseThrow(() -> new Exception("Reserva confirmada pero pago no encontrado."));
            return new PagoResponseDTO(pagoExistente);
        }

        Usuario usuario = reserva.getUsuario();
        PaqueteTuristico paquete = reserva.getPaqueteTuristico();

        // 2. Cambiar Estado de Reserva (RF-09)
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        reservaRepository.save(reserva);

        // 3. Crear el Pago (RF-10)
        Pago nuevoPago = new Pago();
        nuevoPago.setReserva(reserva);
        nuevoPago.setMonto(reserva.getTotal());
        nuevoPago.setEstado(EstadoPago.EXITOSO);
        nuevoPago.setMetodoPago(metodoPago);
        nuevoPago.setReferenciaExterna(referenciaExterna); // ID de MP
        Pago pagoGuardado = pagoRepository.save(nuevoPago);

        // 4. Aplicar Lógica de Puntos (RF-07)

        // 4a. Restar los puntos que se usaron
        if (reserva.getPuntosAUsar() > 0) {
            usuario.setPuntosAcumulados(usuario.getPuntosAcumulados() - reserva.getPuntosAUsar());

            PuntoFidelidad canje = new PuntoFidelidad();
            canje.setUsuario(usuario);
            canje.setCantidadPuntos(-reserva.getPuntosAUsar());
            canje.setMotivo("Canje en Reserva #" + reserva.getId());
            canje.setFechaCanje(java.time.LocalDateTime.now());
            puntoFidelidadRepository.save(canje);
        }

        // 4b. Calcular y sumar los puntos ganados
        BigDecimal totalBruto = paquete.getPrecio().multiply(new BigDecimal(reserva.getCantidadViajeros()));
        int puntosGanados = 0;

        if (usuario.getTipo() == TipoUsuario.PREMIUM || usuario.getTipo() == TipoUsuario.ADMIN) {
            puntosGanados = totalBruto.divide(FACTOR_PUNTOS_PREMIUM).intValue();
        } else { // GRATIS
            puntosGanados = totalBruto.divide(FACTOR_PUNTOS_GRATIS).intValue();
        }

        if (puntosGanados > 0) {
            usuario.setPuntosAcumulados(usuario.getPuntosAcumulados() + puntosGanados);

            PuntoFidelidad ganancia = new PuntoFidelidad();
            ganancia.setUsuario(usuario);
            ganancia.setCantidadPuntos(puntosGanados);
            ganancia.setMotivo("Compra Reserva #" + reserva.getId());
            puntoFidelidadRepository.save(ganancia);
        }

        // 4c. Guardar el estado final del usuario
        usuarioRepository.save(usuario);

        // 5. Enviar Notificación (RF-15)
        String asunto = "¡Tu reserva en InkaTravel está confirmada!";
        String texto = String.format(
                "¡Hola %s!\n\n" +
                        "Tu pago ha sido confirmado exitosamente.\n" +
                        "Reserva ID: %d\n" +
                        "Paquete: %s\n" +
                        "Total Pagado: S/ %.2f\n\n" +
                        "¡Gracias por viajar con nosotros!",
                usuario.getNombre(),
                reserva.getId(),
                paquete.getNombre(),
                reserva.getTotal()
        );
        emailService.enviarEmailSimple(usuario.getCorreo(), asunto, texto);

        return new PagoResponseDTO(pagoGuardado);
    }
}