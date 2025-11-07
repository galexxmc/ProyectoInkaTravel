package com.inkatravel.dto.clima;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
// Ignora cualquier campo del JSON que no tengamos en esta clase
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClimaActualDTO {

    private double temperature;
    private int weathercode; // Un código que dice si está soleado (0), nublado (1,2,3), etc.
}