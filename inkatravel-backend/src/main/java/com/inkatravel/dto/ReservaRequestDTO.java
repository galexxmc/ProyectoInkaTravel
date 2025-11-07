package com.inkatravel.dto;

import lombok.Data;

/**
 * DTO para recibir la petición de crear una reserva (RF-08).
 * Esto es lo que el usuario envía desde el frontend.
 */
@Data
public class ReservaRequestDTO {

    // El ID del paquete que quiere comprar
    private Integer paqueteId;

    // Cuántas personas viajarán
    private int cantidadViajeros;

    // Cuántos puntos de fidelidad desea canjear en esta compra
    private int puntosAUsar;

}