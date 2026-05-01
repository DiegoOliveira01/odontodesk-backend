package com.diego.odontodesk.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppointmentProcedureResponseDTO {

    private Long id;
    private ProcedureResponseDTO procedure;
    private Integer quantity;
}
