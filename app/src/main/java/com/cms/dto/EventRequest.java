package com.cms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record EventRequest(
        @NotBlank @Size(max = 255) String title,
        @Size(max = 1000) String description,
        @NotNull LocalDateTime startDateTime,
        Long tenantId
) {
}
