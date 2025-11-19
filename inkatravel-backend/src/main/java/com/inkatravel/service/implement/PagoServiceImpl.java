package com.inkatravel.service.implement;

import com.inkatravel.dto.PagoResponseDTO;
import com.inkatravel.model.*;
import com.inkatravel.repository.*;
import com.inkatravel.service.EmailService;
import com.inkatravel.service.PagoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;

// --- SDK de Mercado Pago ---
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import java.util.List;
import java.util.Map;

@Service
public class PagoServiceImpl implements PagoService {

    private final ReservaRepository reservaRepository;
    private final PagoRepository pagoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PuntoFidelidadRepository puntoFidelidadRepository;
    private final EmailService emailService;

    private static final BigDecimal FACTOR_PUNTOS_GRATIS = new BigDecimal("10");
    private static final BigDecimal FACTOR_PUNTOS_PREMIUM = new BigDecimal("5");

    @Value("${app.base-url}")
    private String appBaseUrl;

    public PagoServiceImpl(ReservaRepository reservaRepository, PagoRepository pagoRepository,
                           UsuarioRepository usuarioRepository, PuntoFidelidadRepository puntoFidelidadRepository,
                           EmailService emailService) {
        this.reservaRepository = reservaRepository;
        this.pagoRepository = pagoRepository;
        this.usuarioRepository = usuarioRepository;
        this.puntoFidelidadRepository = puntoFidelidadRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public PagoResponseDTO confirmarPagoAdmin(Integer reservaId, String metodoPagoAdmin) throws Exception {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new Exception("Reserva no encontrada"));
        return confirmarLogicaDePago(reserva, metodoPagoAdmin, null);
    }

    @Override
    public String crearLinkDePago(Integer reservaId) throws Exception {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new Exception("Reserva no encontrada"));

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new Exception("Esta reserva ya no está pendiente.");
        }

        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .id(reserva.getPaqueteTuristico().getId().toString())
                .title(reserva.getPaqueteTuristico().getNombre())
                .description("Reserva para " + reserva.getCantidadViajeros() + " viajero(s)")
                .quantity(1)
                .currencyId("PEN")
                .unitPrice(reserva.getTotal())
                .build();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(appBaseUrl + "/pago-exitoso")
                .failure(appBaseUrl + "/pago-fallido")
                .pending(appBaseUrl + "/pago-pendiente")
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(List.of(itemRequest))
                .backUrls(backUrls)
                .autoReturn("approved")
                // --- CORRECCIÓN 1: URL EXPLÍCITA DE RENDER ---
                .notificationUrl("https://inkatravel-backend.onrender.com/api/pagos/webhook")
                // ---------------------------------------------
                .externalReference("RESERVA_ID_" + reserva.getId().toString())
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        return preference.getInitPoint();
    }

    @Override
    @Transactional
    public void procesarWebhook(Long paymentId, String topic) throws Exception {
        if (topic == null || !topic.equals("payment")) {
            return;
        }

        PaymentClient client = new PaymentClient();
        Payment payment = client.get(paymentId);

        if (payment.getStatus() == null || !payment.getStatus().equals("approved")) {
            return;
        }

        String externalReference = payment.getExternalReference();

        if (externalReference.startsWith("RESERVA_ID_")) {
            Integer reservaId = Integer.parseInt(externalReference.replace("RESERVA_ID_", ""));
            Reserva reserva = reservaRepository.findById(reservaId)
                    .orElseThrow(() -> new Exception("Webhook: Reserva no encontrada con ID: " + reservaId));

            confirmarLogicaDePago(reserva, "Mercado Pago (Automático)", paymentId.toString());

        } else if (externalReference.startsWith("SUSCRIPCION_ID_")) {
            Integer usuarioId = Integer.parseInt(externalReference.replace("SUSCRIPCION_ID_", ""));
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new Exception("Webhook Suscripción: Usuario no encontrado"));

            usuario.setTipo(TipoUsuario.PREMIUM);
            usuario.setSuscripcionActiva(true);
            usuarioRepository.save(usuario);

            try {
                emailService.enviarEmailSimple(
                        usuario.getCorreo(),
                        "¡Bienvenido a InkaTravel Premium!",
                        "Hola " + usuario.getNombre() + ",\n\nTu suscripción Premium ha sido activada."
                );
            } catch (Exception e) {
                System.err.println("Error enviando email suscripción (ignorado): " + e.getMessage());
            }

        } else {
            throw new Exception("Webhook: Referencia externa no reconocida: " + externalReference);
        }
    }

    /**
     * LÓGICA CENTRAL BLINDADA
     */
    private PagoResponseDTO confirmarLogicaDePago(Reserva reserva, String metodoPago, String referenciaExterna) throws Exception {

        if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
            Pago pagoExistente = pagoRepository.findByReserva(reserva)
                    .orElseThrow(() -> new Exception("Reserva confirmada pero pago no encontrado."));
            return new PagoResponseDTO(pagoExistente);
        }

        Usuario usuario = reserva.getUsuario();
        PaqueteTuristico paquete = reserva.getPaqueteTuristico();

        // --- CORRECCIÓN 2: CONFIRMAR Y GUARDAR INMEDIATAMENTE ---
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        reservaRepository.saveAndFlush(reserva); // Forzamos escritura en DB
        // --------------------------------------------------------

        Pago nuevoPago = new Pago();
        nuevoPago.setReserva(reserva);
        nuevoPago.setMonto(reserva.getTotal());
        nuevoPago.setEstado(EstadoPago.EXITOSO);
        nuevoPago.setMetodoPago(metodoPago);
        nuevoPago.setReferenciaExterna(referenciaExterna);
        Pago pagoGuardado = pagoRepository.saveAndFlush(nuevoPago);

        // PUNTOS
        if (reserva.getPuntosAUsar() > 0) {
            usuario.setPuntosAcumulados(usuario.getPuntosAcumulados() - reserva.getPuntosAUsar());
            PuntoFidelidad canje = new PuntoFidelidad();
            canje.setUsuario(usuario);
            canje.setCantidadPuntos(-reserva.getPuntosAUsar());
            canje.setMotivo("Canje en Reserva #" + reserva.getId());
            canje.setFechaCanje(LocalDateTime.now());
            puntoFidelidadRepository.save(canje);
        }

        BigDecimal totalBruto = paquete.getPrecio().multiply(new BigDecimal(reserva.getCantidadViajeros()));
        int puntosGanados = 0;

        if (usuario.getTipo() == TipoUsuario.PREMIUM || usuario.getTipo() == TipoUsuario.ADMIN) {
            puntosGanados = totalBruto.divide(FACTOR_PUNTOS_PREMIUM).intValue();
        } else {
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

        usuarioRepository.save(usuario);

        // --- CORRECCIÓN 3: EMAIL EN TRY-CATCH PARA NO HACER ROLLBACK ---
        try {
            emailService.enviarCorreoConfirmacion(
                    usuario.getCorreo(),
                    usuario.getNombre(),
                    reserva.getId(),
                    paquete.getNombre(),
                    reserva.getTotal()
            );
        } catch (Exception e) {
            // Si falla el email, SOLO imprimimos el error.
            // NO lanzamos la excepción, para que la reserva se quede CONFIRMADA.
            System.err.println("ADVERTENCIA CRÍTICA: La reserva se confirmó pero el email falló: " + e.getMessage());
        }
        // ---------------------------------------------------------------

        return new PagoResponseDTO(pagoGuardado);
    }

    @Override
    public String crearLinkSuscripcion(Principal principal) throws Exception {
        Usuario usuario = usuarioRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        if (usuario.getTipo() == TipoUsuario.PREMIUM || usuario.getTipo() == TipoUsuario.ADMIN) {
            throw new Exception("Este usuario ya es Premium o Admin.");
        }

        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .id("PREMIUM-001")
                .title("Suscripción InkaTravel Premium")
                .description("Acceso a beneficios exclusivos por 30 días")
                .quantity(1)
                .currencyId("PEN")
                .unitPrice(new BigDecimal("10.00"))
                .build();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(appBaseUrl + "/suscripcion-exitosa")
                .failure(appBaseUrl + "/pago-fallido")
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(List.of(itemRequest))
                .backUrls(backUrls)
                .autoReturn("approved")
                // --- CORRECCIÓN 1: URL EXPLÍCITA DE RENDER ---
                .notificationUrl("https://inkatravel-backend.onrender.com/api/pagos/webhook")
                // ---------------------------------------------
                .externalReference("SUSCRIPCION_ID_" + usuario.getId().toString())
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        return preference.getInitPoint();
    }
}