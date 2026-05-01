package com.diego.odontodesk.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProcedureResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Integer estimatedDurationMinutes;
    private BigDecimal price;
}
