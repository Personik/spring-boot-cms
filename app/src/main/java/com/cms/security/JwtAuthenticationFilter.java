package com.cms.security;

import com.cms.domain.UserRole;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtTokenService.isValid(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                Claims claims = jwtTokenService.parseClaims(token);
                Long userId = claims.get("userId", Long.class);
                String username = claims.getSubject();
                String role = claims.get("role", String.class);
                Long tenantId = claims.get("tenantId", Long.class);

                if (userId != null && username != null && role != null) {
                    CmsUserPrincipal principal = new CmsUserPrincipal(userId, username, UserRole.valueOf(role), tenantId);
                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                    var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}

