package com.inkatravel.controller;

import com.inkatravel.dto.*;
import com.inkatravel.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inkatravel.dto.MonthlySaleDTO;
import java.util.List;

import com.inkatravel.model.Pago;
import com.inkatravel.service.PagoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.security.Principal;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final AdminService adminService;
    private final PagoService pagoService;

    public AdminController(AdminService adminService, PagoService pagoService) {
        this.adminService = adminService;
        this.pagoService = pagoService;
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerTodosLosUsuarios() {
        List<UsuarioResponseDTO> usuarios = adminService.obtenerTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/reservas")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerTodasLasReservas() {
        List<ReservaResponseDTO> reservas = adminService.obtenerTodasLasReservas();
        return ResponseEntity.ok(reservas);
    }

    @PostMapping("/reservas/{id}/confirmar")
    public ResponseEntity<?> confirmarPago(@PathVariable("id") Integer reservaId) {
        try {
            PagoResponseDTO pagoConfirmadoDTO = pagoService.confirmarPagoAdmin(reservaId, "Yape/Plin (Admin)");
            return ResponseEntity.ok(pagoConfirmadoDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/usuarios/{id}/rol")
    public ResponseEntity<?> actualizarRol(
            @PathVariable Integer id,
            @RequestBody UpdateRoleRequestDTO dto,
            Principal principal) {
        try {
            UsuarioResponseDTO usuarioActualizado = adminService.actualizarRolUsuario(id, dto, principal);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> desactivarUsuario(@PathVariable Integer id) {
        try {
            adminService.desactivarUsuario(id);
            return ResponseEntity.ok("Usuario desactivado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/usuarios/{id}/habilitar")
    public ResponseEntity<?> habilitarUsuario(@PathVariable Integer id) {
        try {
            adminService.habilitarUsuario(id);
            return ResponseEntity.ok("Usuario habilitado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/paquetes")
    public ResponseEntity<List<PaqueteTuristicoResponseDTO>> obtenerTodosLosPaquetes() {
        List<PaqueteTuristicoResponseDTO> paquetes = adminService.obtenerTodosLosPaquetes();
        return ResponseEntity.ok(paquetes);
    }

    @GetMapping("/metrics")
    public ResponseEntity<DashboardMetricsDTO> getDashboardMetrics() {
        DashboardMetricsDTO metrics = adminService.getDashboardMetrics();
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/sales/monthly")
    public ResponseEntity<List<MonthlySaleDTO>> getMonthlySalesData() {
        List<MonthlySaleDTO> salesData = adminService.getMonthlySalesData();
        return ResponseEntity.ok(salesData);
    }

    // ================================================================
    // --- ¡NUEVO! Endpoint "Ping" para evitar que Render se duerma ---
    // URL: https://tubackend.onrender.com/api/admin/ping
    // ================================================================
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Pong!");
    }
}