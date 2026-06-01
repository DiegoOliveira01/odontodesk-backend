package com.diego.odontodesk.dto.request;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AppointmentRequestDTO {

    @NotNull(message = "ID do paciente é obrigatório")
    private Long patientId;

    @NotNull(message = "ID do dentista é obrigatório")
    private Long dentistId;

    @NotNull(message = "Data e hora são obrigatórios")
    @Future(message = "A consulta deve ser agendada para uma data futura")
    private LocalDateTime scheduledAt;

    @NotNull(message = "Duração é obrigatório")
    @Min(value = 15, message = "Duração mínima é 15 minutos")
    private Integer durationMinutes;

    private String notes;

    // Lista de procedimentos é opcional no agendamento
    private List<AppointmentProcedureRequestDTO> procedures;
}
