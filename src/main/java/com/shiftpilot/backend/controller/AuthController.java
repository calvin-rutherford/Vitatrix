package com.shiftpilot.backend.controller;

import com.shiftpilot.backend.dto.AuthRequest;
import com.shiftpilot.backend.dto.AuthResponse;
import com.shiftpilot.backend.model.UserAccount;
import com.shiftpilot.backend.repository.UserAccountRepository;
import com.shiftpilot.backend.security.CustomUserDetailsService;
import com.shiftpilot.backend.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserAccountRepository userAccountRepository;

    @Value("${jwt.expiration:3600}")
    private Integer jwtExpirationSeconds;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        UserAccount user = userAccountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.getUsername()));

        String token = jwtUtil.generateToken(userDetails, user.getRole().name(), user.getFacilityName());

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresInSeconds(jwtExpirationSeconds)
                .role(user.getRole().name())
                .facilityName(user.getFacilityName())
                .build();

        return ResponseEntity.ok(response);
    }
}
