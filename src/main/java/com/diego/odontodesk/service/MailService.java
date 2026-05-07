package com.diego.odontodesk.service;

import com.diego.odontodesk.model.Appointment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j // gera automaticamente: private static final Logger log = LoggerFactory.getLogger(...)
public class MailService {

    public void sendAppointmentConfirmation(Appointment appointment) { // Implementação completa depois
        log.info("E-mail de confirmação para: {}", appointment.getPatient().getEmail());
    }

    public void sendAppointmentCancellation(Appointment appointment) { // Implementação completa depois
        log.info("E-mail de cancelamento para: {}", appointment.getPatient().getEmail());
    }
}
