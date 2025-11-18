// En: com.inkatravel.dto/DashboardMetricsDTO.java

package com.inkatravel.dto;

import lombok.Builder; // Lombok para facilitar la construcción del objeto
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder // Permite construir el objeto fácilmente: DashboardMetricsDTO.builder()...
public class DashboardMetricsDTO {
    private BigDecimal totalVentas;
    private Long paquetesActivos;
    private Long nuevasReservas;
    private Long totalUsuarios;
}