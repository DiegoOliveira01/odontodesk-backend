package com.diego.odontodesk.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DentistResponseDTO {

    private Long id;
    private String name;
    private String cro;
    private String specialty;
    private String phone;
    private String email;
    private LocalDateTime createdAt;
}

