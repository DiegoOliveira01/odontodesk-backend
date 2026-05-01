package com.diego.odontodesk.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PatientResponseDTO {

    private Long id;
    private String name;
    private String cpf;
    private String phone;
    private String email;
    private LocalDate birthDate;
    private String address;
    private LocalDateTime createdAt;
}
