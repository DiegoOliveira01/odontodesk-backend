package com.diego.odontodesk.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcedureRequestDTO {

    @NotBlank(message = "Nome é obrigatorio")
    private String name;

    private String description;

    @Min(value = 1, message = "Duração minima é 1 minuto")
    private Integer estimatedDurationMinutes;

    @DecimalMin(value = "0.0", message = "Preço não pode ser negativo")
    private BigDecimal price;
}
