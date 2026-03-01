package com.cms.dto.auth;

import com.cms.domain.UserRole;

public record AuthLoginResponse(
        String accessToken,
        Long userId,
        UserRole role,
        Long tenantId
) {
}

