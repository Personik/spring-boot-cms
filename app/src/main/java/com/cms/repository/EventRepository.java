package com.cms.repository;

import com.cms.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByTenantIdAndStartDateTimeAfter(Long tenantId, LocalDateTime after, Pageable pageable);

    Page<Event> findByStartDateTimeAfter(LocalDateTime after, Pageable pageable);

    Optional<Event> findByIdAndTenantId(Long id, Long tenantId);
}
