package com.cms.controller;

import com.cms.dto.EventRequest;
import com.cms.dto.EventResponse;
import com.cms.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public Page<EventResponse> list(Pageable pageable) {
        return eventService.listUpcomingEvents(pageable);
    }

    @GetMapping("/{eventId}")
    public EventResponse getOne(@PathVariable Long eventId) {
        return eventService.getEvent(eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('USER', 'SUPERADMIN')")
    public EventResponse create(@Valid @RequestBody EventRequest request) {
        return eventService.createEvent(request);
    }

    @PutMapping("/{eventId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPERADMIN')")
    public EventResponse update(@PathVariable Long eventId, @Valid @RequestBody EventRequest request) {
        return eventService.updateEvent(eventId, request);
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('USER', 'SUPERADMIN')")
    public void delete(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
    }
}
