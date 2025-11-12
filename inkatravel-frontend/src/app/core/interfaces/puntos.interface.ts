export interface PuntoFidelidadResponseDTO {
    id: number;
    cantidadPuntos: number;
    motivo: string;
    fechaOtorgamiento: string; // O Date
    fechaCanje: string | null; // Puede ser nulo
    usuarioId: number;
}