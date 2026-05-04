package com.diego.odontodesk.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorResponseDTO {

    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;

    // Usado só para erros de validação - campo → mensagem de erro
    // Ex: {"cpf": "CPF deve ter 11 digitos", "name": "Nome é obrigatorio"}
    private Map<String, String> fieldErrors;
}
