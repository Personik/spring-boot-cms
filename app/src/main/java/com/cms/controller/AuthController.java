package com.cms.controller;

import com.cms.dto.UserResponse;
import com.cms.dto.auth.AuthLoginRequest;
import com.cms.dto.auth.AuthLoginResponse;
import com.cms.dto.auth.RegisterRequest;
import com.cms.service.AuthService;
import com.cms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public AuthLoginResponse login(@Valid @RequestBody AuthLoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }
}

