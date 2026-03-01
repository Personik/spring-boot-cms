package com.cms.security;

import com.cms.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CmsUserPrincipal {

    private final Long userId;
    private final String username;
    private final UserRole role;
    private final Long tenantId;
}

