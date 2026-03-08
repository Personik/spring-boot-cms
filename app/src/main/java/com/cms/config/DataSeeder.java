package com.cms.config;

import com.cms.domain.Event;
import com.cms.domain.Post;
import com.cms.domain.Tenant;
import com.cms.domain.User;
import com.cms.domain.UserRole;
import com.cms.repository.EventRepository;
import com.cms.repository.PostRepository;
import com.cms.repository.TenantRepository;
import com.cms.repository.UserRepository;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final EventRepository eventRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already seeded, skipping.");
            return;
        }

        log.info("Seeding database with sample data...");

        Tenant tenant = new Tenant();
        tenant.setName("Acme Corp");
        tenant = tenantRepository.save(tenant);

        User normalUser = new User();
        normalUser.setUsername("editor");
        normalUser.setPassword(passwordEncoder.encode("editor123"));
        normalUser.setRole(UserRole.USER);
        normalUser.setTenant(tenant);
        normalUser = userRepository.save(normalUser);

        User superadmin = new User();
        superadmin.setUsername("admin");
        superadmin.setPassword(passwordEncoder.encode("admin123"));
        superadmin.setRole(UserRole.SUPERADMIN);
        superadmin.setTenant(null);
        superadmin = userRepository.save(superadmin);

        Post post1 = new Post();
        post1.setTitle("Getting Started with Spring Boot");
        post1.setDescription("A beginner-friendly introduction to building REST APIs with Spring Boot.");
        post1.setContent(
                "Spring Boot makes it easy to create stand-alone, production-grade Spring-based applications. "
                + "In this post we walk through setting up a project with Spring Initializr, creating your first "
                + "REST controller, connecting to a database with Spring Data JPA, and running the application "
                + "with embedded Tomcat. By the end you will have a fully working CRUD API."
        );
        post1.setTenant(tenant);
        post1.setAuthor(normalUser);
        postRepository.save(post1);

        Post post2 = new Post();
        post2.setTitle("Understanding Multi-Tenancy in Web Applications");
        post2.setDescription("How shared-database multi-tenancy works and why it matters.");
        post2.setContent(
                "Multi-tenancy allows a single application instance to serve multiple customers (tenants) while "
                + "keeping their data isolated. The simplest approach is a shared database with a tenant_id "
                + "discriminator column on every tenant-scoped table. Each query is filtered by the current "
                + "tenant, which is resolved from the authenticated user's JWT token. This architecture is "
                + "cost-effective and easy to maintain for small-to-medium SaaS products."
        );
        post2.setTenant(tenant);
        post2.setAuthor(normalUser);
        postRepository.save(post2);

        Post post3 = new Post();
        post3.setTitle("Securing REST APIs with JWT");
        post3.setDescription("A practical guide to stateless authentication using JSON Web Tokens.");
        post3.setContent(
                "JSON Web Tokens (JWT) provide a compact, URL-safe way of representing claims between two "
                + "parties. When a user logs in, the server generates a signed token containing the user's ID, "
                + "role, and tenant information. The client sends this token in the Authorization header on "
                + "every subsequent request. The server validates the signature and extracts claims without "
                + "needing server-side sessions, making the API fully stateless and horizontally scalable."
        );
        post3.setTenant(tenant);
        post3.setAuthor(superadmin);
        postRepository.save(post3);

        Event event1 = new Event();
        event1.setTitle("Spring Boot Workshop");
        event1.setDescription("Hands-on workshop covering Spring Boot fundamentals and best practices.");
        event1.setStartDateTime(LocalDateTime.now().plusDays(14));
        event1.setTenant(tenant);
        event1.setAuthor(normalUser);
        eventRepository.save(event1);

        Event event2 = new Event();
        event2.setTitle("Tech Conference 2026");
        event2.setDescription("Annual technology conference with talks on microservices, cloud, and AI.");
        event2.setStartDateTime(LocalDateTime.now().plusMonths(2));
        event2.setTenant(tenant);
        event2.setAuthor(normalUser);
        eventRepository.save(event2);

        Event event3 = new Event();
        event3.setTitle("Security Best Practices Webinar");
        event3.setDescription("Live webinar on securing REST APIs, JWT handling, and OWASP top 10.");
        event3.setStartDateTime(LocalDateTime.now().plusDays(30));
        event3.setTenant(tenant);
        event3.setAuthor(superadmin);
        eventRepository.save(event3);

        log.info("Seeding complete: 1 tenant, 2 users, 3 posts, 3 events.");
    }
}
