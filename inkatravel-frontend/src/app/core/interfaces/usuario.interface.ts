/**
 * DTO (Interfaz) para la respuesta del Perfil de Usuario.
 */
export interface UsuarioResponseDTO {
    id: number;
    nombre: string;
    correo: string;
    tipo: 'GRATIS' | 'PREMIUM' | 'ADMIN';
    puntosAcumulados: number;
    suscripcionActiva: boolean;
    activo: boolean;
}