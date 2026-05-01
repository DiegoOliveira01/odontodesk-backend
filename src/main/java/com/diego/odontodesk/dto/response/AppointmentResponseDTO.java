package com.diego.odontodesk.dto.response;

import com.diego.odontodesk.model.AppointmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AppointmentResponseDTO {

    private Long id;
    private PatientResponseDTO patient;      // objeto completo, não só o ID
    private DentistResponseDTO dentist;      // objeto completo, não só o ID
    private LocalDateTime scheduledAt;
    private Integer durationMinutes;
    private AppointmentStatus status;
    private String notes;
    private List<AppointmentProcedureResponseDTO> procedures;
    private LocalDateTime createdAt;
}
