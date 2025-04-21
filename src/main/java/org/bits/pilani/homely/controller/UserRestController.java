package org.bits.pilani.homely.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bits.pilani.homely.dto.SignupRequest;
import org.bits.pilani.homely.dto.SignupResponse;
import org.bits.pilani.homely.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = userService.signup(request);
        return ResponseEntity.ok(response);
    }
}