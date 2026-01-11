package com.hrms.auth.controller;

import com.hrms.auth.dto.CreateUserRequest;
import com.hrms.auth.dto.LoginRequest;
import com.hrms.auth.dto.LoginResponse;
import com.hrms.auth.dto.RefreshTokenRequest;
import com.hrms.auth.service.AuthService;
import com.hrms.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success("Login successful", authService.login(request))
        );
    }


    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Token refreshed",
                        authService.refreshToken(request)
                )
        );
    }


    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequest request) {

        authService.logout(request.getRefreshToken());

        return ResponseEntity.ok(
                ApiResponse.success("Logged out successfully", null)
        );
    }


    @PostMapping("/users")
    public ResponseEntity<ApiResponse<Void>> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        authService.createUser(request);

        return ResponseEntity.status(201)
                .body(ApiResponse.success("User created successfully", null));
    }
}
