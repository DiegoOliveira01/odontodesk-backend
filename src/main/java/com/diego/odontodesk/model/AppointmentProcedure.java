package com.diego.odontodesk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "appointment_procedure")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentProcedure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "procedure_id")
    private Procedure procedure;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;
}
