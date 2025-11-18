package com.inkatravel.service.implement;

import com.inkatravel.dto.*;
import com.inkatravel.model.EstadoReserva;
import com.inkatravel.model.Usuario; // <-- IMPORTAR
import com.inkatravel.repository.PaqueteTuristicoRepository;
import com.inkatravel.repository.ReservaRepository;
import com.inkatravel.repository.UsuarioRepository;
import com.inkatravel.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inkatravel.dto.MonthlySaleDTO; // <-- ¡NUEVO IMPORTE!
import java.util.List; // <-- ¡NUEVO IMPORTE!

import java.math.BigDecimal;
import java.security.Principal; // <-- IMPORTAR


import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final PaqueteTuristicoRepository paqueteRepository;

    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;

    public AdminServiceImpl(UsuarioRepository usuarioRepository, ReservaRepository reservaRepository, PaqueteTuristicoRepository paqueteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
        this.paqueteRepository = paqueteRepository; // <-- Añadir
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> obtenerTodosLosUsuarios() {
        // 1. Busca todos los usuarios
        return usuarioRepository.findAll().stream()
                // 2. Convierte cada Usuario a UsuarioResponseDTO
                .map(UsuarioResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> obtenerTodasLasReservas() {
        // 1. Busca todas las reservas
        return reservaRepository.findAll().stream()
                // 2. Convierte cada Reserva a ReservaResponseDTO
                .map(ReservaResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizarRolUsuario(Integer id, UpdateRoleRequestDTO dto, Principal adminPrincipal) throws Exception {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // Regla de seguridad: Un admin no puede cambiarse el rol a sí mismo.
        if (usuario.getCorreo().equals(adminPrincipal.getName())) {
            throw new Exception("No puedes modificar tu propio rol.");
        }

        usuario.setTipo(dto.getNuevoRol()); // Asigna el nuevo rol
        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return new UsuarioResponseDTO(usuarioActualizado);
    }

    @Override
    @Transactional
    public void desactivarUsuario(Integer id) throws Exception {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // (Podríamos añadir una regla para no banear Admins, pero por simpleza lo dejamos así)

        usuario.setActivo(false); // Lo "banea"
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void habilitarUsuario(Integer id) throws Exception {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        usuario.setActivo(true); // Lo "reactiva"
        usuarioRepository.save(usuario);
    }

    /**
     * (NUEVO - RF-12) Obtiene TODOS los paquetes (activos e inactivos)
     */
    @Override
    @Transactional(readOnly = true)
    public List<PaqueteTuristicoResponseDTO> obtenerTodosLosPaquetes() {
        return paqueteRepository.findAll().stream()
                .map(PaqueteTuristicoResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * (NUEVO) Obtiene todas las métricas clave para el Dashboard de Administración.
     */
    @Override
    @Transactional(readOnly = true)
    public DashboardMetricsDTO getDashboardMetrics() {

        // 1. Total Ventas (Ingresos) - Usa la solución final con el parámetro Enum
        BigDecimal totalVentas = reservaRepository.sumTotalConfirmedSales(EstadoReserva.CONFIRMADA);
        if (totalVentas == null) {
            totalVentas = BigDecimal.ZERO;
        }

        // 2. Paquetes Activos (disponibilidad = true)
        Long paquetesActivos = paqueteRepository.countByDisponibilidadTrue();

        // 3. Nuevas Reservas (Últimos 7 días)
        LocalDateTime haceSieteDias = LocalDateTime.now().minusDays(7);
        Long nuevasReservas = reservaRepository.countByFechaReservaAfter(haceSieteDias);

        // 4. Total Usuarios
        Long totalUsuarios = usuarioRepository.count();

        // Construir y devolver el DTO
        return DashboardMetricsDTO.builder()
                .totalVentas(totalVentas)
                .paquetesActivos(paquetesActivos)
                .nuevasReservas(nuevasReservas)
                .totalUsuarios(totalUsuarios)
                .build();
    }

    /**
     * (NUEVO) Implementación para obtener datos del gráfico de ventas.
     */
    @Override
    @Transactional(readOnly = true)
    public List<MonthlySaleDTO> getMonthlySalesData() {
        // 1. Definir el rango de fechas (últimos 6 meses)
        LocalDateTime startDate = LocalDateTime.now().minusMonths(6).withDayOfMonth(1);

        // 2. Llamar al repositorio
        return reservaRepository.findMonthlySalesAfter(EstadoReserva.CONFIRMADA, startDate);
    }


}