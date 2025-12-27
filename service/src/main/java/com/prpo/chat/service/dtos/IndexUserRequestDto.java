package com.prpo.chat.service.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IndexUserRequestDto {
    @NotBlank
    private String id;

    @NotBlank
    private String username;
}
