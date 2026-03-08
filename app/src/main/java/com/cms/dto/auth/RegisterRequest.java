package com.cms.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 100) String username,
        @NotBlank @Size(min = 6) String password,
        @NotNull Long tenantId
) {
}
