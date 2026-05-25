package com.diego.odontodesk.service;

import com.diego.odontodesk.model.Appointment;
import com.diego.odontodesk.model.AppointmentProcedure;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${mail.from.name}")
    private String mailFromName;

    // Formatters para exibir data e hora no template
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    // @Async — envia o e-mail em uma thread separada
    // O AppointmentService não fica bloqueado esperando o envio terminar
    @Async
    public void sendAppointmentConfirmation(Appointment appointment) {
        try {
            // 1. Monta o contexto — variáveis que o template Thymeleaf vai usar
            Context context = new Context();
            context.setVariable("patientName",
                    appointment.getPatient().getName());
            context.setVariable("dentistName",
                    "Dr(a). " + appointment.getDentist().getName());
            context.setVariable("date",
                    appointment.getScheduledAt().format(DATE_FORMATTER));
            context.setVariable("time",
                    appointment.getScheduledAt().format(TIME_FORMATTER));
            context.setVariable("duration",
                    appointment.getDurationMinutes());
            context.setVariable("notes",
                    appointment.getNotes());

            // Monta a string de procedimentos se houver
            // Ex: "Limpeza (x1), Restauração (x2)"
            if (!appointment.getProcedures().isEmpty()) {
                String procedures = appointment.getProcedures()
                        .stream()
                        .map(ap -> ap.getProcedure().getName() +
                                " (x" + ap.getQuantity() + ")")
                        .collect(Collectors.joining(", "));
                context.setVariable("procedures", procedures);
            }

            // 2. Processa o template HTML com os dados do contexto
            String htmlContent = templateEngine.process(
                    "mail/appointment-confirmation", context);

            // 3. Envia o e-mail
            sendEmail(
                    appointment.getPatient().getEmail(),
                    "✅ Consulta Agendada — OdontoDesk",
                    htmlContent
            );

            log.info("E-mail de confirmação enviado para: {}",
                    appointment.getPatient().getEmail());

        } catch (Exception e) {
            // Loga o erro mas não lança exception
            // Falha no e-mail não deve cancelar o agendamento
            log.error("Erro ao enviar e-mail de confirmação para {}: {}",
                    appointment.getPatient().getEmail(), e.getMessage());
        }
    }

    @Async
    public void sendAppointmentCancellation(Appointment appointment) {
        try {
            Context context = new Context();
            context.setVariable("patientName",
                    appointment.getPatient().getName());
            context.setVariable("dentistName",
                    "Dr(a). " + appointment.getDentist().getName());
            context.setVariable("date",
                    appointment.getScheduledAt().format(DATE_FORMATTER));
            context.setVariable("time",
                    appointment.getScheduledAt().format(TIME_FORMATTER));

            String htmlContent = templateEngine.process(
                    "mail/appointment-cancellation", context);

            sendEmail(
                    appointment.getPatient().getEmail(),
                    "❌ Consulta Cancelada — OdontoDesk",
                    htmlContent
            );

            log.info("E-mail de cancelamento enviado para: {}",
                    appointment.getPatient().getEmail());

        } catch (Exception e) {
            log.error("Erro ao enviar e-mail de cancelamento para {}: {}",
                    appointment.getPatient().getEmail(), e.getMessage());
        }
    }

    // Método privado reutilizável para envio — evita repetição
    private void sendEmail(String to, String subject, String htmlContent)
            throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();

        // MimeMessageHelper — facilita montar e-mails com HTML
        // true = multipart (necessário para HTML)
        // "UTF-8" = encoding para caracteres especiais
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(mailFrom, mailFromName);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = é HTML, não texto plano

        mailSender.send(message);
    }
}