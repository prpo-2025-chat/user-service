package com.prpo.chat.service.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordHashDto {
    private String password;
    @NotBlank
    private String hashedPassword;
}
