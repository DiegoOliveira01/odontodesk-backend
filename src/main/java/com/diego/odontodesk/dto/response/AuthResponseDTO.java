package com.diego.odontodesk.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {

    private String token;
    private String email;
    private String role;
}
