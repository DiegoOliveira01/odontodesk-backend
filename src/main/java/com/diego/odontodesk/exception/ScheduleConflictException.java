package com.diego.odontodesk.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScheduleConflictException extends RuntimeException {

  public ScheduleConflictException(LocalDateTime start, LocalDateTime end) {
        super(String.format(
                "Conflito de horario: dentista já possui consulta entre %s e %s",
                start.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                end.format(DateTimeFormatter.ofPattern("HH:mm"))
        ));
    }
}
