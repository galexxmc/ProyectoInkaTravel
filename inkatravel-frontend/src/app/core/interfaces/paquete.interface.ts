import { OpenMeteoResponseDTO } from "./clima.interface";

// DTO del paquete (de la BD)
export interface PaqueteTuristicoResponseDTO {
    id: number;
    nombre: string;
    descripcion: string;
    precio: number;
    region: string;
    categoria: string;
    itinerario: string;
    disponibilidad: boolean;
    latitud: number;
    longitud: number;
}

// DTO "Contenedor" para la vista de detalle
export interface PaqueteDetalleResponseDTO {
    paquete: PaqueteTuristicoResponseDTO;
    clima: OpenMeteoResponseDTO;
}