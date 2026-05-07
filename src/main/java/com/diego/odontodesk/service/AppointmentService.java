package com.diego.odontodesk.service;

import com.diego.odontodesk.dto.request.AppointmentRequestDTO;
import com.diego.odontodesk.dto.request.AppointmentStatusUpdateDTO;
import com.diego.odontodesk.dto.response.*;
import com.diego.odontodesk.exception.BusinessException;
import com.diego.odontodesk.exception.InvalidStatusTransitionException;
import com.diego.odontodesk.exception.ResourceNotFoundException;
import com.diego.odontodesk.exception.ScheduleConflictException;
import com.diego.odontodesk.model.*;
import com.diego.odontodesk.repository.AppointmentRepository;
import com.diego.odontodesk.repository.DentistRepository;
import com.diego.odontodesk.repository.PatientRepository;
import com.diego.odontodesk.repository.ProcedureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AppointmentService {


    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final ProcedureRepository procedureRepository;
    private final MailService mailService;

    // Mapa de transições válidas — chave: status atual, valor: status permitidos
    // Esta sendo usado Set para verificação O(1) — mais eficiente que List

    private static final java.util.Map<AppointmentStatus, Set<AppointmentStatus>>
            VALID_TRANSITIONS = java.util.Map.of(
                    AppointmentStatus.AGENDADA, Set.of(AppointmentStatus.CONFIRMADA, AppointmentStatus.CANCELADA),
                    AppointmentStatus.CONFIRMADA, Set.of(AppointmentStatus.CONCLUIDA, AppointmentStatus.CANCELADA),
                    AppointmentStatus.CANCELADA, Set.of(), // estado final
                    AppointmentStatus.CONCLUIDA, Set.of()  // estado final
            );

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> findAll(){
        return appointmentRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDTO findById(Long id){
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta", id));
        return toResponseDTO(appointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> findByDentist(Long dentistId){
        return appointmentRepository.findByDentistId(dentistId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> findByDentistAndDataRange(
            Long dentistId,
            LocalDateTime start,
            LocalDateTime end){
        return appointmentRepository
                .findByDentistIdAndScheduleAtBetween(dentistId, start, end)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public AppointmentResponseDTO create(AppointmentRequestDTO dto){

        // 1 - Busca as entidades relacionadas
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", dto.getPatientId()));

        Dentist dentist = dentistRepository.findById(dto.getDentistId())
                .orElseThrow(() -> new ResourceNotFoundException("Dentista", dto.getDentistId()));

        // 2 - Calcula o horario de fim de uma nova consulta
        LocalDateTime endTime = dto.getScheduledAt()
                .plusMinutes(dto.getDurationMinutes());

        // 3 - Verifica conflito de horario
        // excludeId = null porque é uma criação, não há consulta a ignorar
        boolean hasConflict = appointmentRepository.hasScheduleConflict(
                dentist.getId(),
                dto.getScheduledAt(),
                endTime,
                null
        );

        if (hasConflict){
            throw new ScheduleConflictException(dto.getScheduledAt(), endTime);
        }

        // 4 - Monta a entidade Appointment
        Appointment appointment = Appointment.builder()
                .patient(patient)
                .dentist(dentist)
                .scheduledAt(dto.getScheduledAt())
                .durationMinutes(dto.getDurationMinutes())
                .notes(dto.getNotes())
                .build();
        // Status agendado é setado pelo @PrePersist da entidade

        // 5 - Adiciona os procedimentos se forem informados
        if (dto.getProcedures() != null && !dto.getProcedures().isEmpty()){
            for (var procDTO : dto.getProcedures()){
                Procedure procedure = procedureRepository.findById(procDTO.getProcedureId())
                        .orElseThrow(() -> new ResourceNotFoundException("Procedimento", procDTO.getProcedureId()));

                AppointmentProcedure ap = AppointmentProcedure.builder()
                        .appointment(appointment)
                        .procedure(procedure)
                        .quantity(procDTO.getQuantity())
                        .build();

                appointment.getProcedures().add(ap);
            }
        }

        // 6 - Salva o cascade ALL cuida de salvar os AppointmentProcedure também
        Appointment saved = appointmentRepository.save(appointment);

        // 7 Enviar o Email de confirmação para o paciente
        // só enviar se o paciente tiver email cadastrado
        if (patient.getEmail() != null){
            mailService.sendAppointmentConfirmation(saved);
        }

        return toResponseDTO(saved);
    }

    @Transactional
    public AppointmentResponseDTO update(Long id, AppointmentRequestDTO dto){

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta", id));

        // Não permite editar consultas que já foram concluidas ou canceladas
        if (appointment.getStatus() == AppointmentStatus.CANCELADA || appointment.getStatus() == AppointmentStatus.CONCLUIDA){
            throw new BusinessException("Não é possivel editar uma consulta com status: " + appointment.getStatus());
        }

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", dto.getPatientId()));

        Dentist dentist = dentistRepository.findById(dto.getDentistId())
                .orElseThrow(() -> new ResourceNotFoundException("Dentista", dto.getDentistId()));

        LocalDateTime endTime = dto.getScheduledAt().plusMinutes(dto.getDurationMinutes());

        // exludeId = id / ignora a propria consulta na verificação de conflito
        boolean hasConflict = appointmentRepository.hasScheduleConflict(
                dentist.getId(),
                dto.getScheduledAt(),
                endTime,
                id
        );

        if (hasConflict){
            throw new ScheduleConflictException(dto.getScheduledAt(), endTime);
        }

        appointment.setPatient(patient);
        appointment.setDentist(dentist);
        appointment.setScheduledAt(dto.getScheduledAt());
        appointment.setDurationMinutes(dto.getDurationMinutes());
        appointment.setNotes(dto.getNotes());

        // Limpa os procedimentos antigos e adiciona os novos
        // O orphanRemoval=true da entidade deleta os antigos do banco automaticamente
        appointment.getProcedures().clear();

        if (dto.getProcedures() != null && !dto.getProcedures().isEmpty()) {
            for (var procDTO : dto.getProcedures()) {
                Procedure procedure = procedureRepository.findById(procDTO.getProcedureId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Procedimento", procDTO.getProcedureId()));

                AppointmentProcedure ap = AppointmentProcedure.builder()
                        .appointment(appointment)
                        .procedure(procedure)
                        .quantity(procDTO.getQuantity())
                        .build();

                appointment.getProcedures().add(ap);
            }
        }

        return toResponseDTO(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponseDTO updateStatus(Long id, AppointmentStatusUpdateDTO dto){
        Appointment appointment = appointmentRepository .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta", id));

        AppointmentStatus currentStatus = appointment.getStatus();
        AppointmentStatus targetStatus = dto.getStatus();

        // Verifica de a transi~~ao é valida usando o mapa definido acima
        Set<AppointmentStatus> allowed = VALID_TRANSITIONS.get(currentStatus);
        if (!allowed.contains(targetStatus)){
            throw new InvalidStatusTransitionException(currentStatus, targetStatus);
        }

        appointment.setStatus(targetStatus);
        Appointment updated = appointmentRepository.save(appointment);

        // Envia o Email quando a consulta é cancelada
        if (targetStatus == AppointmentStatus.CANCELADA && appointment.getPatient().getEmail() != null){
            mailService.sendAppointmentCancellation(updated);
        }

        return toResponseDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Consulta", id);
        }
        appointmentRepository.deleteById(id);
    }

    // ------------------------------------------------------------------
    // Conversão de entidade para DTO — monta o objeto completo aninhado
    // ------------------------------------------------------------------
    private AppointmentResponseDTO toResponseDTO(Appointment appointment){

        List<AppointmentProcedureResponseDTO> procedures = appointment.getProcedures()
                .stream()
                .map(ap -> AppointmentProcedureResponseDTO.builder()
                        .id(ap.getId())
                        .procedure(ProcedureResponseDTO.builder()
                                .id(ap.getProcedure().getId())
                                .name(ap.getProcedure().getName())
                                .description(ap.getProcedure().getDescription())
                                .estimatedDurationMinutes(ap.getProcedure().getEstimatedDurationMinutes())
                                .price(ap.getProcedure().getPrice())
                                .build())
                        .quantity(ap.getQuantity())
                        .build())
                .toList();

        return AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .patient(PatientResponseDTO.builder()
                        .id(appointment.getPatient().getId())
                        .name(appointment.getPatient().getName())
                        .cpf(appointment.getPatient().getCpf())
                        .phone(appointment.getPatient().getPhone())
                        .email(appointment.getPatient().getEmail())
                        .birthDate(appointment.getPatient().getBirthDate())
                        .createdAt(appointment.getPatient().getCreatedAt())
                        .build())
                .dentist(DentistResponseDTO.builder()
                        .id(appointment.getDentist().getId())
                        .name(appointment.getDentist().getName())
                        .cro(appointment.getDentist().getCro())
                        .specialty(appointment.getDentist().getSpecialty())
                        .phone(appointment.getDentist().getPhone())
                        .email(appointment.getDentist().getEmail())
                        .createdAt(appointment.getDentist().getCreatedAt())
                        .build())
                .scheduledAt(appointment.getScheduledAt())
                .durationMinutes(appointment.getDurationMinutes())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .procedures(procedures)
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}
