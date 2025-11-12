/**
 * DTO para la Petición (Request) de crear una reserva (RF-08).
 * Esto es lo que el frontend (Angular) ENVÍA al backend.
 */
export interface ReservaRequestDTO {
    paqueteId: number;
    cantidadViajeros: number;
    puntosAUsar: number;
}

/**
 * DTO para la Respuesta (Response) de una reserva.
 * Esto es lo que el backend DEVUELVE.
 */
export interface ReservaResponseDTO {
    id: number;
    fechaReserva: string; // O Date, pero string es más seguro para JSON
    estado: 'PENDIENTE' | 'CONFIRMADA' | 'CANCELADA';
    cantidadViajeros: number;
    total: number;
    puntosAUsar: number;
    
    // IDs de las relaciones
    usuarioId: number;
    usuarioNombre: string;
    paqueteId: number;
    paqueteNombre: string;
}