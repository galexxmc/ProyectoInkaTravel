package com.inkatravel.controller;

import com.inkatravel.dto.*;
import com.inkatravel.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inkatravel.model.Pago; // <-- IMPORTAR
import com.inkatravel.service.PagoService; // <-- IMPORTAR
import org.springframework.http.HttpStatus; // <-- IMPORTAR
import org.springframework.web.bind.annotation.PathVariable; // <-- IMPORTAR
import org.springframework.web.bind.annotation.PostMapping; // <-- IMPORTAR

import org.springframework.web.bind.annotation.DeleteMapping; // <-- IMPORTAR
import org.springframework.web.bind.annotation.PutMapping; // <-- IMPORTAR
import org.springframework.web.bind.annotation.RequestBody; // <-- IMPORTAR
import java.security.Principal; // <-- IMPORTAR

import java.util.List;

@RestController
@RequestMapping("/api/admin") // URL base para TODO lo de admin
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final AdminService adminService;
    private final PagoService pagoService;

    public AdminController(AdminService adminService, PagoService pagoService) {
        this.adminService = adminService;
        this.pagoService = pagoService;
    }

    /**
     * Endpoint para RF-12: Ver todos los usuarios.
     * PROTEGIDO (Requiere JWT de ADMIN).
     * Escuchará en: GET http://localhost:8080/api/admin/usuarios
     */
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerTodosLosUsuarios() {
        List<UsuarioResponseDTO> usuarios = adminService.obtenerTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Endpoint para RF-12: Ver todas las reservas.
     * PROTEGIDO (Requiere JWT de ADMIN).
     * Escuchará en: GET http://localhost:8080/api/admin/reservas
     */
    @GetMapping("/reservas")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerTodasLasReservas() {
        List<ReservaResponseDTO> reservas = adminService.obtenerTodasLasReservas();
        return ResponseEntity.ok(reservas);
    }

    /**
     * (NUEVO) Endpoint para RF-09/10/07: Confirmación Manual de Pago.
     * PROTEGIDO (Requiere JWT de ADMIN).
     * Escuchará en: POST http://localhost:8080/api/admin/reservas/5/confirmar
     */
    @PostMapping("/reservas/{id}/confirmar")
    public ResponseEntity<?> confirmarPago(@PathVariable("id") Integer reservaId) {
        try {
            // Esta variable ahora es de tipo DTO
            PagoResponseDTO pagoConfirmadoDTO = pagoService.confirmarPagoAdmin(reservaId, "Yape/Plin (Admin)");

            // Devolvemos el DTO (que es seguro)
            return ResponseEntity.ok(pagoConfirmadoDTO);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * (NUEVO) Endpoint para RF-12: Actualizar rol de usuario.
     * PROTEGIDO (Requiere JWT de ADMIN).
     * Escuchará en: PUT http://localhost:8080/api/admin/usuarios/5/rol
     */
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

    /**
     * (NUEVO) Endpoint para RF-12: Desactivar (banear) usuario.
     * PROTEGIDO (Requiere JWT de ADMIN).
     * Escuchará en: DELETE http://localhost:8080/api/admin/usuarios/5
     */
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> desactivarUsuario(@PathVariable Integer id) {
        try {
            adminService.desactivarUsuario(id);
            return ResponseEntity.ok("Usuario desactivado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * (NUEVO) Endpoint para RF-12: Habilitar (reactivar) usuario.
     * PROTEGIDO (Requiere JWT de ADMIN).
     * Escuchará en: PUT http://localhost:8080/api/admin/usuarios/5/habilitar
     */
    @PutMapping("/usuarios/{id}/habilitar")
    public ResponseEntity<?> habilitarUsuario(@PathVariable Integer id) {
        try {
            adminService.habilitarUsuario(id);
            return ResponseEntity.ok("Usuario habilitado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * (NUEVO) Endpoint para RF-12: Ver todos los paquetes (activos e inactivos).
     */
    @GetMapping("/paquetes")
    public ResponseEntity<List<PaqueteTuristicoResponseDTO>> obtenerTodosLosPaquetes() {
        List<PaqueteTuristicoResponseDTO> paquetes = adminService.obtenerTodosLosPaquetes();
        return ResponseEntity.ok(paquetes);
    }
}