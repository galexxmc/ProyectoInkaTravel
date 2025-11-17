// En: src/app/core/interfaces/paquete.interface.ts

import { OpenMeteoResponseDTO } from "./clima.interface";

// DTO del paquete (de la BD) - CORREGIDO
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
    
    // --- ¡SOLUCIÓN! AÑADIR IMAGEN URL ---
    imagenUrl: string | null; // El backend envía el nombre/ruta de la imagen
    // ------------------------------------
}

// DTO "Contenedor" para la vista de detalle
export interface PaqueteDetalleResponseDTO {
    paquete: PaqueteTuristicoResponseDTO;
    clima: OpenMeteoResponseDTO;
}