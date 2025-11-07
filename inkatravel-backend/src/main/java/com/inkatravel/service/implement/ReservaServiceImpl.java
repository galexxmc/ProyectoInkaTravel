package com.inkatravel.service.implement;

import com.inkatravel.dto.ReservaRequestDTO;
import com.inkatravel.model.*;
import com.inkatravel.repository.PaqueteTuristicoRepository;
import com.inkatravel.repository.PuntoFidelidadRepository;
import com.inkatravel.repository.ReservaRepository;
import com.inkatravel.repository.UsuarioRepository;
import com.inkatravel.service.ReservaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ¡Importante!

import java.util.List; // <-- NUEVA IMPORTACIÓN
import java.util.stream.Collectors; // <-- NUEVA IMPORTACIÓN

import com.inkatravel.dto.ReservaResponseDTO;

import java.math.BigDecimal;
import java.security.Principal;

@Service
public class ReservaServiceImpl implements ReservaService {

    // Necesitamos todos los repositorios para esta lógica
    private final UsuarioRepository usuarioRepository;
    private final PaqueteTuristicoRepository paqueteRepository;
    private final ReservaRepository reservaRepository;
    private final PuntoFidelidadRepository puntoFidelidadRepository;

    // Constantes para la Lógica Simple de Puntos (RF-07)
    private static final BigDecimal FACTOR_PUNTOS_GRATIS = new BigDecimal("10"); // 1 punto x cada 10 soles
    private static final BigDecimal FACTOR_PUNTOS_PREMIUM = new BigDecimal("5"); // 1 punto x cada 5 soles (Doble)
    private static final BigDecimal FACTOR_CANJE_GRATIS = new BigDecimal("0.1"); // 1 punto = 0.10 soles
    private static final BigDecimal FACTOR_CANJE_PREMIUM = new BigDecimal("0.15"); // 1 punto = 0.15 soles

    public ReservaServiceImpl(UsuarioRepository usuarioRepository,
                              PaqueteTuristicoRepository paqueteRepository,
                              ReservaRepository reservaRepository,
                              PuntoFidelidadRepository puntoFidelidadRepository) {
        this.usuarioRepository = usuarioRepository;
        this.paqueteRepository = paqueteRepository;
        this.reservaRepository = reservaRepository;
        this.puntoFidelidadRepository = puntoFidelidadRepository;
    }

    /**
     * (VERSIÓN CORREGIDA)
     * Ahora SÓLO calcula el total y crea la reserva PENDIENTE.
     * NO toca los puntos del usuario.
     */
    @Override
    @Transactional
    public ReservaResponseDTO crearReserva(ReservaRequestDTO reservaDTO, Principal principal) throws Exception {

        // 1. OBTENER LOS DATOS
        Usuario usuario = usuarioRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        PaqueteTuristico paquete = paqueteRepository.findById(reservaDTO.getPaqueteId())
                .orElseThrow(() -> new Exception("Paquete turístico no encontrado"));

        // 2. CALCULAR COSTOS Y PUNTOS (La Lógica Simple)
        BigDecimal totalBruto = paquete.getPrecio().multiply(new BigDecimal(reservaDTO.getCantidadViajeros()));
        BigDecimal descuentoPorPuntos = BigDecimal.ZERO;

        if (reservaDTO.getPuntosAUsar() > 0) {
            if (reservaDTO.getPuntosAUsar() > usuario.getPuntosAcumulados()) {
                throw new Exception("No puedes usar más puntos de los que tienes.");
            }

            if (usuario.getTipo() == TipoUsuario.PREMIUM || usuario.getTipo() == TipoUsuario.ADMIN) {
                descuentoPorPuntos = new BigDecimal(reservaDTO.getPuntosAUsar()).multiply(FACTOR_CANJE_PREMIUM);
            } else { // Es GRATIS
                descuentoPorPuntos = new BigDecimal(reservaDTO.getPuntosAUsar()).multiply(FACTOR_CANJE_GRATIS);
            }
        }

        BigDecimal totalNeto = totalBruto.subtract(descuentoPorPuntos);
        if (totalNeto.compareTo(BigDecimal.ZERO) < 0) {
            totalNeto = BigDecimal.ZERO;
        }

        // --- LÓGICA DE PUNTOS ELIMINADA DE AQUÍ ---
        // (Ya no se toca usuario.puntosAcumulados)
        // (Ya no se llama a puntoFidelidadRepository.save(...))

        // 4. CREAR LA RESERVA (en estado PENDIENTE)
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setUsuario(usuario);
        nuevaReserva.setPaqueteTuristico(paquete);
        nuevaReserva.setCantidadViajeros(reservaDTO.getCantidadViajeros());
        nuevaReserva.setTotal(totalNeto); // Guarda el total NETO (con descuento)
        nuevaReserva.setEstado(EstadoReserva.PENDIENTE);
        nuevaReserva.setPuntosAUsar(reservaDTO.getPuntosAUsar()); // SÍ guardamos cuántos puntos usará

        Reserva reservaGuardada = reservaRepository.save(nuevaReserva);
        return new ReservaResponseDTO(reservaGuardada);
    }
    /**
     * (NUEVO) Implementación de RF-11
     */
    @Override
    @Transactional(readOnly = true) // Es una operación de solo lectura, es más eficiente
    public List<ReservaResponseDTO> obtenerMisReservas(Principal principal) throws Exception {

        // 1. Encontrar al usuario logueado
        Usuario usuario = usuarioRepository.findByCorreo(principal.getName())
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // 2. Buscar en la BD todas las reservas para ese usuario
        //    (Usamos el método que ya creamos en el Paso 4: 'UsuarioRepository')
        //    (Corrección: El método estaba en ReservaRepository)
        List<Reserva> misReservas = reservaRepository.findByUsuario(usuario);

        // 3. Convertir la lista de Entidades (Reserva) a una lista de DTOs (ReservaResponseDTO)
        //    (Esto es VITAL para evitar el error LAZY/403)
        return misReservas.stream()
                .map(ReservaResponseDTO::new) // Llama al constructor del DTO (Reserva -> ReservaResponseDTO)
                .collect(Collectors.toList());
    }

}