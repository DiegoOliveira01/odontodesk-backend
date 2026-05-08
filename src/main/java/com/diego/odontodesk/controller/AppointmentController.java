package com.diego.odontodesk.controller;

import com.diego.odontodesk.dto.request.AppointmentRequestDTO;
import com.diego.odontodesk.dto.request.AppointmentStatusUpdateDTO;
import com.diego.odontodesk.dto.response.AppointmentResponseDTO;
import com.diego.odontodesk.model.Appointment;
import com.diego.odontodesk.model.AppointmentStatus;
import com.diego.odontodesk.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> findAll(){
        return ResponseEntity.ok(appointmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(appointmentService.findById(id));
    }

    // GET /api/appointments/dentist/3
    // Retorna todas as consultas de um dentista específico
    @GetMapping("/dentist/{dentistId}")
    public ResponseEntity<List<AppointmentResponseDTO>> findByDentist(@PathVariable Long dentistId){
        return ResponseEntity.ok(appointmentService.findByDentist(dentistId));
    }

    // GET /api/appointments/dentist/3/range?start=2025-01-01T00:00:00&end=2025-01-31T23:59:59
    // Retorna consultas de um dentista em um intervalo de datas — usado pelo calendário
    @GetMapping("/dentist/{dentistId}/range")
    public ResponseEntity<List<AppointmentResponseDTO>> findByDentistAndDataRange(
            @PathVariable Long dentistId,
            // @DateTimeFormat instrui o Spring a converter a String da URL para LocalDateTime
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end){

        return ResponseEntity.ok(appointmentService.findByDentistAndDataRange(dentistId, start, end));
    }

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> create(
            @Valid @RequestBody AppointmentRequestDTO dto){

        AppointmentResponseDTO created = appointmentService.create(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> update(
            @PathVariable Long id, @Valid @RequestBody AppointmentRequestDTO dto){
        return ResponseEntity.ok(appointmentService.update(id, dto));
    }

    // PATCH — atualiza apenas o status, não o recurso inteiro
    // PUT /api/appointments/3       → atualiza todos os dados da consulta
    // PATCH /api/appointments/3/status → atualiza só o status
    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponseDTO> updateStatus(
            @PathVariable Long id, @Valid @RequestBody AppointmentStatusUpdateDTO dto){

        return ResponseEntity.ok(appointmentService.updateStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        appointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
