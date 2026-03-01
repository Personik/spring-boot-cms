package com.cms.service;

import com.cms.domain.UserRole;
import com.cms.dto.auth.AuthLoginRequest;
import com.cms.dto.auth.AuthLoginResponse;
import com.cms.repository.UserRepository;
import com.cms.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthLoginResponse login(AuthLoginRequest request) {
        var user = userRepository.findByUsername(request.username()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        if (user.getRole() == UserRole.USER && user.getTenant() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user tenant configuration");
        }

        String accessToken = jwtTokenService.generateToken(user);
        Long tenantId = user.getTenant() != null ? user.getTenant().getId() : null;

        return new AuthLoginResponse(accessToken, user.getId(), user.getRole(), tenantId);
    }
}

