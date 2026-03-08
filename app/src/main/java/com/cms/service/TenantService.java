package com.cms.service;

import com.cms.domain.Tenant;
import com.cms.dto.TenantRequest;
import com.cms.dto.TenantResponse;
import com.cms.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    @Transactional(readOnly = true)
    public Page<TenantResponse> listTenants(Pageable pageable) {
        return tenantRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public TenantResponse getTenant(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
        return toResponse(tenant);
    }

    @Transactional
    public TenantResponse createTenant(TenantRequest request) {
        if (tenantRepository.findByName(request.name()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tenant name already exists");
        }
        Tenant tenant = new Tenant();
        tenant.setName(request.name());
        return toResponse(tenantRepository.save(tenant));
    }

    @Transactional
    public TenantResponse updateTenant(Long tenantId, TenantRequest request) {
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));

        tenantRepository.findByName(request.name()).ifPresent(existing -> {
            if (!existing.getId().equals(tenantId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Tenant name already exists");
            }
        });

        tenant.setName(request.name());
        return toResponse(tenantRepository.save(tenant));
    }

    @Transactional
    public void deleteTenant(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
        tenantRepository.delete(tenant);
    }

    private TenantResponse toResponse(Tenant tenant) {
        return new TenantResponse(tenant.getId(), tenant.getName());
    }
}
