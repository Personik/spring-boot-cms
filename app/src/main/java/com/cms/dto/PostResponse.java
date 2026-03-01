package com.cms.dto;

public record PostResponse(
        Long id,
        String title,
        String description,
        String content,
        Long tenantId,
        Long authorId
        ) {
}

