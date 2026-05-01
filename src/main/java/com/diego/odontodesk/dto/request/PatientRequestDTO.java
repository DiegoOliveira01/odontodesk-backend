package com.diego.odontodesk.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientRequestDTO {
    @NotBlank(message = "Nome é obrigatorio")
    private String name;

    @NotBlank(message = "CPF é obrigatorio")
    @Size(min = 11, max = 11, message = "CPF deve ter 11 digitos")
    private String cpf;

    private String phone;

    @Email(message = "Email invalido")
    private String email;

    private LocalDate birthDate;

    private String address;
}
