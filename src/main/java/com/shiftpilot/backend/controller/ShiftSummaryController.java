package com.shiftpilot.backend.controller;

import com.shiftpilot.backend.dto.ShiftSummaryResponse;
import com.shiftpilot.backend.model.NursingNote;
import com.shiftpilot.backend.model.ShiftSummary;
import com.shiftpilot.backend.service.ShiftSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/shift-summaries")
@RequiredArgsConstructor
public class ShiftSummaryController {

    private final ShiftSummaryService shiftSummaryService;

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'NURSE')")
    public ShiftSummaryResponse generateSummary(
            @RequestParam NursingNote.Shift shift,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) String nurseUsername,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Authentication authentication) {

        String effectiveNurseUsername = resolveNurseUsername(nurseUsername, authentication);

        ShiftSummary summary = shiftSummaryService.generateSummary(shift, patientId, effectiveNurseUsername, from, to);
        return mapToResponse(summary);
    }

    @GetMapping("/latest")
    @PreAuthorize("hasAnyRole('ADMIN', 'NURSE')")
    public ShiftSummaryResponse getLatestSummary(
            @RequestParam NursingNote.Shift shift,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) String nurseUsername,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Authentication authentication) {

        String effectiveNurseUsername = resolveNurseUsername(nurseUsername, authentication);

        return shiftSummaryService.getLatestSummary(shift, patientId, effectiveNurseUsername, from, to)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No summary found matching the filters"));
    }

    private String resolveNurseUsername(String requestUsername, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!isAdmin) {
            // NURSE requested
            if (requestUsername != null && !requestUsername.equals(authentication.getName())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Nurses can only request summaries for themselves");
            }
            return authentication.getName(); // Force to self
        }

        // Admin can request any, or all if null
        return requestUsername;
    }

    private ShiftSummaryResponse mapToResponse(ShiftSummary summary) {
        return new ShiftSummaryResponse(
                summary.getId(),
                summary.getShift(),
                summary.getDate(),
                summary.getGeneratedAt(),
                summary.getContent());
    }
}
