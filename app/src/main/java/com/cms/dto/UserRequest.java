package com.cms.dto;

import com.cms.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank @Size(max = 100) String username,
        @NotBlank @Size(min = 6) String password,
        @NotNull UserRole role,
        Long tenantId
) {
}
