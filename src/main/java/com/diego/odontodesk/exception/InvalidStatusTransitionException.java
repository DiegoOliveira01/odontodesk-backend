package com.diego.odontodesk.exception;

import com.diego.odontodesk.model.AppointmentStatus;

public class InvalidStatusTransitionException extends RuntimeException {

  public InvalidStatusTransitionException(AppointmentStatus current, AppointmentStatus target) {
        super(String.format(
                "Transição de status invalida: %s -> %s",
                current, target
        ));
    }
}
