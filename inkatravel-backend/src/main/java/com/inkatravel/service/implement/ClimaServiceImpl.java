package com.inkatravel.service.implement;

import com.inkatravel.dto.clima.OpenMeteoResponseDTO;
import com.inkatravel.service.ClimaService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;

@Service
public class ClimaServiceImpl implements ClimaService {

    // Inyectamos el "teléfono" (RestTemplate) que creamos en AppConfig
    private final RestTemplate restTemplate;

    // Esta es la URL base de la API de Open-Meteo (es gratis y sin llave)
    private static final String API_URL = "https://api.open-meteo.com/v1/forecast";

    public ClimaServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public OpenMeteoResponseDTO obtenerClimaActual(BigDecimal latitud, BigDecimal longitud) {
        // 1. Construimos la URL completa con los parámetros
        // Ejemplo: ...?latitude=-16.40&longitude=-71.53&current_weather=true
        String urlCompleta = String.format("%s?latitude=%s&longitude=%s&current_weather=true",
                API_URL, latitud.toString(), longitud.toString());

        try {
            // 2. Hacemos la llamada GET
            // Le pedimos a RestTemplate que llame a la URL y convierta
            // el JSON de respuesta automáticamente a nuestra clase DTO.
            return restTemplate.getForObject(urlCompleta, OpenMeteoResponseDTO.class);

        } catch (Exception e) {
            // Si la API falla, registramos el error y devolvemos null
            // (En un proyecto real, manejaríamos esto mejor)
            System.err.println("Error al llamar a la API de Open-Meteo: " + e.getMessage());
            return null;
        }
    }
}