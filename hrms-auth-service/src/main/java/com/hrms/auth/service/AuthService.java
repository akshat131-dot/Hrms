package com.hrms.auth.service;

import com.hrms.auth.dto.CreateUserRequest;
import com.hrms.auth.dto.LoginRequest;
import com.hrms.auth.dto.LoginResponse;
import com.hrms.auth.dto.RefreshTokenRequest;
import com.hrms.auth.entity.RefreshToken;
import com.hrms.auth.entity.Role;
import com.hrms.auth.entity.User;
import com.hrms.auth.repository.RefreshTokenRepository;
import com.hrms.auth.repository.RoleRepository;
import com.hrms.auth.repository.UserRepository;
import com.hrms.auth.security.JwtUtil;
import com.hrms.common.exception.BusinessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserRepository userRepository,
            RoleRepository roleRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* ================= LOGIN ================= */

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(user.getId())
                        .token(refreshToken)
                        .expiryDate(LocalDateTime.now().plusDays(7))
                        .revoked(false)
                        .build()
        );

        return new LoginResponse(accessToken, refreshToken);
    }



    public void createUser(CreateUserRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("USERNAME_EXISTS", "Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("EMAIL_EXISTS", "Email already exists");
        }

        // 2. Determine who is making the request
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);

        boolean isAdminExecuting = isAuthenticated && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Set<Role> roles = new HashSet<>();
        boolean containsEmployeeRole = false;

        // 3. Process Roles and apply your Business Logic
        for (String roleName : request.getRoles()) {
            switch (roleName.toUpperCase()) {
                case "ADMIN":

                    break;

                case "HR":
                    if (!isAdminExecuting) {
                        throw new BusinessException("FORBIDDEN", "Only an Admin can create an HR account");
                    }
                    break;

                case "EMPLOYEE":

                    if (request.getEmployeeId() == null ) {
                        throw new BusinessException("EMPLOYEE_ID_REQUIRED", "Employee ID is required for employee registration");
                    }
                    containsEmployeeRole = true;
                    break;

                default:
                    throw new BusinessException("INVALID_ROLE", "Invalid role: " + roleName);
            }

            Role role = roleRepository.findByName(roleName.toUpperCase())
                    .orElseGet(() -> roleRepository.save(new Role(roleName.toUpperCase())));
            roles.add(role);
        }

        // 4. Create and Save User
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(roles);

        // Link the Employee ID if it was provided
        if (request.getEmployeeId() != null) {
            user.setEmployeeId(request.getEmployeeId());
        }

        userRepository.save(user);
    }



    public LoginResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BusinessException("INVALID_TOKEN", "Refresh token not found"));

        if (storedToken.isRevoked()) {
            throw new BusinessException("TOKEN_REVOKED", "Please login again");
        }

        String username = jwtUtil.extractUsername(request.getRefreshToken());
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));

        if (!jwtUtil.isTokenValid(request.getRefreshToken(), user)) {
            throw new BusinessException("INVALID_TOKEN", "Invalid refresh token");
        }

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        String newAccessToken = jwtUtil.generateAccessToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken(user);

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(user.getId())
                        .token(newRefreshToken)
                        .expiryDate(LocalDateTime.now().plusDays(7))
                        .revoked(false)
                        .build()
        );

        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    public void logout(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BusinessException("INVALID_TOKEN", "Refresh token not found"));
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }
}