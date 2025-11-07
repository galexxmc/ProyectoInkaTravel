package com.inkatravel.dto;

import com.inkatravel.model.TipoUsuario; // Importa tu Enum
import lombok.Data;

/**
 * DTO para recibir el nuevo rol que el Admin quiere asignar.
 */
@Data
public class UpdateRoleRequestDTO {
    // Debe coincidir con los valores de tu Enum (GRATIS, PREMIUM, ADMIN)
    private TipoUsuario nuevoRol;
}