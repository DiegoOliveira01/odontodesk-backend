package com.diego.odontodesk.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DentistRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "CRO é obrigatório")
    private String cro;

    private String specialty;

    private String phone;

    @Email(message = "Email invalido")
    private String email;
}
