package org.bits.pilani.homely.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bits.pilani.homely.dto.LoginRequest;
import org.bits.pilani.homely.dto.LoginResponse;
import org.bits.pilani.homely.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String roles = userDetails.getAuthorities().iterator().next().getAuthority();
            String token = jwtUtil.generateToken(userDetails.getUsername(), roles);

            return ResponseEntity.ok(LoginResponse.builder()
                    .token(token)
                .username(userDetails.getUsername())
                .role(userDetails.getAuthorities().iterator().next().getAuthority())
                .message("Login successful")
                .build());
        } catch (Exception e) {
            log.error("Login failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.badRequest()
                .body(LoginResponse.builder()
                    .message("Invalid username or password")
                    .build());
        }
    }
}