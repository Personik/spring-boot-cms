package com.cms.dto;

import com.cms.domain.UserRole;

public record UserResponse(
        Long id,
        String username,
        UserRole role,
        Long tenantId
) {
}
