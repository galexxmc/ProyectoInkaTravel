// Define el objeto "current_weather"
export interface ClimaActualDTO {
    temperature: number;
    weathercode: number;
}

// Define la respuesta raíz de Open-Meteo
export interface OpenMeteoResponseDTO {
    latitude: number;
    longitude: number;
    current_weather: ClimaActualDTO; // Mapeado desde "current_weather" en el backend
}