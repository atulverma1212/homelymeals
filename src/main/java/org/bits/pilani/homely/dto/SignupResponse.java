package org.bits.pilani.homely.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignupResponse {
    private Long userId;
    private String username;
    private String message;
}