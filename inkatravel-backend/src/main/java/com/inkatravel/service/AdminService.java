package com.inkatravel.service;

import com.inkatravel.dto.ReservaResponseDTO;
import com.inkatravel.dto.UpdateRoleRequestDTO; // <-- IMPORTAR
import com.inkatravel.dto.UsuarioResponseDTO;
import java.security.Principal; // <-- IMPORTAR
import java.util.List;

/**
 * Define la lógica de negocio para las operaciones del Administrador (RF-12).
 */
public interface AdminService {

    /**
     * (RF-12) Obtiene la lista de TODOS los usuarios registrados.
     * @return Lista de usuarios en formato DTO.
     */
    List<UsuarioResponseDTO> obtenerTodosLosUsuarios();

    /**
     * (RF-12) Obtiene la lista de TODAS las reservas del sistema.
     * @return Lista de reservas en formato DTO.
     */
    List<ReservaResponseDTO> obtenerTodasLasReservas();

    /**
     * (NUEVO - RF-12) Actualiza el rol de un usuario.
     * @param id El ID del usuario a modificar.
     * @param dto El DTO que contiene el nuevo rol.
     * @param adminPrincipal El Admin que realiza la acción (para evitar auto-modificación).
     * @return El usuario actualizado.
     */
    UsuarioResponseDTO actualizarRolUsuario(Integer id, UpdateRoleRequestDTO dto, Principal adminPrincipal) throws Exception;

    /**
     * (NUEVO - RF-12) Desactiva (banea) a un usuario.
     * @param id El ID del usuario a desactivar.
     */
    void desactivarUsuario(Integer id) throws Exception;

    /**
     * (NUEVO - RF-12) Reactiva a un usuario que estaba desactivado (baneado).
     * @param id El ID del usuario a reactivar.
     */
    void habilitarUsuario(Integer id) throws Exception;
}