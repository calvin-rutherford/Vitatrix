package com.shiftpilot.backend.controller;

import com.shiftpilot.backend.dto.UserDto;
import com.shiftpilot.backend.model.UserAccount;
import com.shiftpilot.backend.model.Role;
import com.shiftpilot.backend.repository.UserAccountRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        if (userAccountRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        Role role;
        try {
            role = Role.valueOf(userDto.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role");
        }

        UserAccount user = UserAccount.builder()
                .username(userDto.getUsername())
                .passwordHash(passwordEncoder.encode(userDto.getPassword()))
                .role(role)
                .facilityName(userDto.getFacilityName())
                .build();

        userAccountRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDto(user));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> listUsers() {
        List<UserDto> users = userAccountRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    private UserDto mapToDto(UserAccount user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .facilityName(user.getFacilityName())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
