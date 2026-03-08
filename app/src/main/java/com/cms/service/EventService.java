package com.cms.service;

import com.cms.domain.Event;
import com.cms.domain.Tenant;
import com.cms.domain.User;
import com.cms.dto.EventRequest;
import com.cms.dto.EventResponse;
import com.cms.repository.EventRepository;
import com.cms.repository.TenantRepository;
import com.cms.repository.UserRepository;
import com.cms.security.CmsUserPrincipal;
import com.cms.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<EventResponse> listUpcomingEvents(Long requestTenantId, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        Long tenantId = resolveReadTenantId(requestTenantId);
        if (tenantId == null) {
            return eventRepository.findByStartDateTimeAfter(now, pageable).map(this::toResponse);
        }
        return eventRepository.findByTenantIdAndStartDateTimeAfter(tenantId, now, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public EventResponse getEvent(Long eventId, Long requestTenantId) {
        Long tenantId = resolveReadTenantId(requestTenantId);
        Event event;
        if (tenantId == null) {
            event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        } else {
            event = eventRepository.findByIdAndTenantId(eventId, tenantId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        }
        return toResponse(event);
    }

    @Transactional
    public EventResponse createEvent(EventRequest request) {
        Tenant tenant = resolveTenantForWrite(request.tenantId());
        User author = requireCurrentUser();

        Event event = new Event();
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setStartDateTime(request.startDateTime());
        event.setTenant(tenant);
        event.setAuthor(author);

        return toResponse(eventRepository.save(event));
    }

    @Transactional
    public EventResponse updateEvent(Long eventId, EventRequest request) {
        Event event = findEventWithAccessCheck(eventId);

        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setStartDateTime(request.startDateTime());

        return toResponse(eventRepository.save(event));
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        Event event = findEventWithAccessCheck(eventId);
        eventRepository.delete(event);
    }

    private Long resolveReadTenantId(Long requestTenantId) {
        Long contextTenantId = TenantContext.getTenantId();
        if (contextTenantId != null) {
            return contextTenantId;
        }
        if (isAuthenticated()) {
            return requestTenantId;
        }
        if (requestTenantId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tenantId query parameter is required");
        }
        return requestTenantId;
    }

    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getPrincipal() instanceof CmsUserPrincipal;
    }

    private Event findEventWithAccessCheck(Long eventId) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            return eventRepository.findById(eventId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        }
        return eventRepository.findByIdAndTenantId(eventId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));
    }

    private Tenant resolveTenantForWrite(Long requestTenantId) {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId != null) {
            return tenantRepository.findById(tenantId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
        }
        if (requestTenantId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tenantId is required for superadmin event creation");
        }
        return tenantRepository.findById(requestTenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
    }

    private User requireCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CmsUserPrincipal principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getStartDateTime(),
                event.getTenant().getId(),
                event.getAuthor().getId()
        );
    }
}
