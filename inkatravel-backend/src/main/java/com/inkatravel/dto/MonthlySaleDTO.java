// En: com.inkatravel.dto/MonthlySaleDTO.java

package com.inkatravel.dto;

import java.math.BigDecimal;

// Interfaz para que Spring Data JPA mapee los resultados de la consulta
public interface MonthlySaleDTO {
    Integer getYear();
    Integer getMonth();
    BigDecimal getTotal();
}