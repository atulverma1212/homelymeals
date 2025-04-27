package org.bits.pilani.homely.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String username;
    private String role;
    private String message;
    private long userId;
}