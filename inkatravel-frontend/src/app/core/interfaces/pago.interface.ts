/**
 * DTO (Interfaz) para la respuesta de un Pago (RF-10).
 * Esto es lo que el backend DEVUELVE cuando se confirma un pago.
 */
export interface PagoResponseDTO {
    id: number;
    metodoPago: string;
    monto: number;
    fechaPago: string; // O Date
    estado: 'EXITOSO' | 'FALLIDO' | 'PENDIENTE';
    referenciaExterna: string | null; // El ID de Mercado Pago
    reservaId: number; // Solo el ID de la reserva
}