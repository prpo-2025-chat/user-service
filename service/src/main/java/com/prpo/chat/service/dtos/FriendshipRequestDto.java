package com.prpo.chat.service.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FriendshipRequestDto {
    @NotBlank
    private String firstUserId;

    @NotBlank
    private String secondUserId;
}
