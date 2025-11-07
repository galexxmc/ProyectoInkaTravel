package com.inkatravel.dto.clima;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty; // ¡Importante!
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMeteoResponseDTO {

    private double latitude;
    private double longitude;

    // Le dice a Jackson que el JSON "current_weather"
    // debe mapearse a este campo "climaActual".
    @JsonProperty("current_weather")
    private ClimaActualDTO climaActual;
}