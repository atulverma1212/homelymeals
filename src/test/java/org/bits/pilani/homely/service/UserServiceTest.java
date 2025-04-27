package org.bits.pilani.homely.service;

import org.bits.pilani.homely.dto.SignupRequest;
import org.bits.pilani.homely.dto.SignupResponse;
import org.bits.pilani.homely.entity.Role;
import org.bits.pilani.homely.entity.User;
import org.bits.pilani.homely.repository.RoleRepository;
import org.bits.pilani.homely.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private SignupRequest validSignupRequest;
    private User savedUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        // Set up valid signup request
        validSignupRequest = new SignupRequest();
        validSignupRequest.setUsername("testuser");
        validSignupRequest.setPassword("password123");
        validSignupRequest.setContactNumber("9876543210");

        // Set up user role
        userRole = new Role();
        userRole.setId(1L);
        userRole.setName("USER");

        // Set up saved user
        savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setPassword("encodedPassword");
        savedUser.setContactnumber("+919876543210");
        savedUser.setRoles(Collections.singleton(userRole));
    }

    @Test
    void signup_ValidRequest_ReturnsSignupResponse() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        SignupResponse response = userService.signup(validSignupRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("testuser", response.getUsername());
        assertEquals("User registered successfully", response.getMessage());

        verify(userRepository).findByUsername("testuser");
        verify(roleRepository).findByName("USER");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signup_ExistingUsername_ThrowsResponseStatusException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(savedUser));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.signup(validSignupRequest);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Username already exists", exception.getReason());

        verify(userRepository).findByUsername("testuser");
        verify(roleRepository, never()).findByName(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signup_RoleNotFound_ThrowsResponseStatusException() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.signup(validSignupRequest);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Default role not found", exception.getReason());

        verify(userRepository).findByUsername("testuser");
        verify(roleRepository).findByName("USER");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getCustomerById_ExistingUserWithUserRole_ReturnsUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));

        // Act
        User result = userService.getCustomerById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());

        verify(userRepository).findById(1L);
    }

    @Test
    void getCustomerById_NonExistingUser_ThrowsResponseStatusException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.getCustomerById(999L);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("User not found", exception.getReason());

        verify(userRepository).findById(999L);
    }

    @Test
    void getCustomerById_UserWithoutUserRole_ThrowsResponseStatusException() {
        // Arrange
        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setName("ADMIN");

        User adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("admin");
        adminUser.setRoles(Collections.singleton(adminRole));

        when(userRepository.findById(2L)).thenReturn(Optional.of(adminUser));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            userService.getCustomerById(2L);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Access denied: User does not have USER role", exception.getReason());

        verify(userRepository).findById(2L);
    }
}
