package com.cms.controller;

import com.cms.dto.TenantRequest;
import com.cms.dto.TenantResponse;
import com.cms.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    public Page<TenantResponse> list(Pageable pageable) {
        return tenantService.listTenants(pageable);
    }

    @GetMapping("/{tenantId}")
    public TenantResponse getOne(@PathVariable Long tenantId) {
        return tenantService.getTenant(tenantId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TenantResponse create(@Valid @RequestBody TenantRequest request) {
        return tenantService.createTenant(request);
    }

    @PutMapping("/{tenantId}")
    public TenantResponse update(@PathVariable Long tenantId, @Valid @RequestBody TenantRequest request) {
        return tenantService.updateTenant(tenantId, request);
    }

    @DeleteMapping("/{tenantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long tenantId) {
        tenantService.deleteTenant(tenantId);
    }
}
