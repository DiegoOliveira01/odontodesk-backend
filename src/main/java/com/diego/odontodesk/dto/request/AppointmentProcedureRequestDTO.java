package com.diego.odontodesk.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentProcedureRequestDTO {

    @NotNull(message = "ID do procedimento é obrigatorio")
    private Long procedureId;

    @Min(value = 1, message = "Quantidade minima é 1")
    private Integer quantity = 1;
}
