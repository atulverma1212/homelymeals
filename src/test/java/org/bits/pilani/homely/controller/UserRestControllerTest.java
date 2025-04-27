package org.bits.pilani.homely.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bits.pilani.homely.dto.SignupRequest;
import org.bits.pilani.homely.dto.SignupResponse;
import org.bits.pilani.homely.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserRestControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserRestController userRestController;

    private SignupRequest validSignupRequest;
    private SignupResponse successResponse;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(userRestController)
                .setControllerAdvice(new org.bits.pilani.homely.exception.HomelyExceptionHandler())
                .build();

        // Initialize ObjectMapper
        objectMapper = new ObjectMapper();

        // Set up valid signup request
        validSignupRequest = new SignupRequest();
        validSignupRequest.setUsername("testuser");
        validSignupRequest.setPassword("password123");
        validSignupRequest.setContactNumber("9876543210");

        // Set up success response
        successResponse = new SignupResponse(1L, "testuser", "User registered successfully");
    }

    @Test
    void signup_ValidRequest_ReturnsSuccessResponse() throws Exception {
        // Arrange
        when(userService.signup(any(SignupRequest.class))).thenReturn(successResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSignupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.message", is("User registered successfully")));
    }

    @Test
    void signup_UsernameExists_ReturnsBadRequest() throws Exception {
        // Arrange
        when(userService.signup(any(SignupRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSignupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Username already exists")));
    }

    @Test
    void signup_RoleNotFound_ReturnsInternalServerError() throws Exception {
        // Arrange
        when(userService.signup(any(SignupRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Default role not found"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSignupRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is("Default role not found")));
    }
}