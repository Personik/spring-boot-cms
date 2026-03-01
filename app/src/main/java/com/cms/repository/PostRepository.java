package com.cms.repository;

import com.cms.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByTenantId(Long tenantId, Pageable pageable);

    Optional<Post> findByIdAndTenantId(Long id, Long tenantId);
}

