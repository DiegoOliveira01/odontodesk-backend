package com.diego.odontodesk.dto.request;

import com.diego.odontodesk.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {

    @NotBlank(message = "Email é obrigatorio")
    @Email(message = "Email invalido")
    private String email;

    @NotBlank(message = "Senha é obrigatoria")
    @Size(min = 6, message = "Senha deve ter no minimo 6 caracteres")
    private String password;

    @NotNull(message = "Role é obrigatoria")
    private UserRole role;
}
