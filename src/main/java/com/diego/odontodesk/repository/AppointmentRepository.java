package com.diego.odontodesk.repository;

import com.diego.odontodesk.model.Appointment;
import com.diego.odontodesk.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Busca por dentista
    List<Appointment> findByDentistId(Long dentistId);

    // Busca pro paciente
    List<Appointment> findByPatientId(Long patientIId);

    // Busca por status
    List<Appointment> findByStatus(AppointmentStatus status);

    // Busca por dentista e status
    List<Appointment> findByDentistIdAndStatus(Long dentistId, AppointmentStatus status);

    // Busca consultas de um dentista em um intervalo de datas
    List<Appointment> findByDentistIdAndScheduledAtBetween(
            Long dentistId,
            LocalDateTime start,
            LocalDateTime end
    );

    // Query de conflito de horário, Importante!

    @Query(value = """
    SELECT COUNT(*) > 0 FROM appointments a
    WHERE a.dentist_id = :dentistId
    AND a.status NOT IN ('CANCELADA', 'CONCLUIDA')
    AND a.scheduled_at < :newEnd
    AND (a.scheduled_at + (a.duration_minutes * INTERVAL '1 minute')) > :newStart
    AND (:excludeId IS NULL OR a.id <> :excludeId)
""", nativeQuery = true)
    boolean hasScheduleConflict(
            @Param("dentistId") Long dentistId,
            @Param("newStart") LocalDateTime newStart,
            @Param("newEnd") LocalDateTime newEnd,
            @Param("excludeId") Long excludeId
    );

}
