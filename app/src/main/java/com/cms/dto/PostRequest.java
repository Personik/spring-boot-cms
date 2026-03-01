package com.cms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostRequest(
        @NotBlank @Size(max = 255) String title,
        @Size(max = 1000) String description,
        @NotBlank String content,
        Long tenantId
) {
}
