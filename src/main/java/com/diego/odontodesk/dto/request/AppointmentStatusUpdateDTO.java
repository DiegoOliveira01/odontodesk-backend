package com.diego.odontodesk.dto.request;

import com.diego.odontodesk.model.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppointmentStatusUpdateDTO {

    @NotNull(message = "Status é obrigatório")
    private AppointmentStatus status;
}
