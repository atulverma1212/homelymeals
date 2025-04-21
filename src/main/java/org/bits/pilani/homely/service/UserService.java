package org.bits.pilani.homely.service;

import lombok.RequiredArgsConstructor;
import org.bits.pilani.homely.dto.SignupRequest;
import org.bits.pilani.homely.dto.SignupResponse;
import org.bits.pilani.homely.entity.Role;
import org.bits.pilani.homely.entity.User;
import org.bits.pilani.homely.repository.RoleRepository;
import org.bits.pilani.homely.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponse signup(SignupRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Default role not found"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setContactnumber("+91" + request.getContactNumber());
        user.setRoles(Collections.singleton(userRole));

        User savedUser = userRepository.save(user);

        return new SignupResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                "User registered successfully"
        );
    }

    public User getCustomerById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRoles().stream().noneMatch(role -> role.getName().equals("USER"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: User does not have USER role");
        }

        return user;
    }
}