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
    List<Appointment> findByDentistIdAndScheduleAtBetween(
            Long dentistId,
            LocalDateTime start,
            LocalDateTime end
    );

    // Query de conflito de horário, Importante!

    @Query("""
        SELECT COUNT(a) > 0 FROM Appointment a
        WHERE a.dentist.id = :dentistId
        AND a.status NOT IN ('CANCELADA', CONCLUIDA)
        AND a.scheduleAt < :newEnd
        AND FUNCTION('timestampadd', SECOND, a.durationMinutes * 60, a.scheduleAt) > :newStart
        AND (:excludeId IS NULL OR a.id <> :excludeId)
    """)

    boolean hasScheduleConflict(
            @Param("dentistId") Long dentistId,
            @Param("newStart") LocalDateTime newStart,
            @Param("newEnd") LocalDateTime newEnd,
            @Param("excludeId") Long excludeId
    );

}
