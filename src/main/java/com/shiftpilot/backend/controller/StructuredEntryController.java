package com.shiftpilot.backend.controller;

import com.shiftpilot.backend.dto.StructuredEntryResponse;
import com.shiftpilot.backend.dto.StructuredEntryUpdateRequest;
import com.shiftpilot.backend.model.StructuredEntry;
import com.shiftpilot.backend.service.StructuredEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/structured-entries")
@RequiredArgsConstructor
public class StructuredEntryController {

    private final StructuredEntryService structuredEntryService;

    // Helper endpoint to list entries for verification dashboard
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NURSE')")
    public List<StructuredEntryResponse> getEntriesByPatient(@PathVariable Long patientId) {
        return structuredEntryService.getEntriesByPatientId(patientId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NURSE')")
    public StructuredEntryResponse updateEntry(@PathVariable Long id,
            @RequestBody @jakarta.validation.Valid StructuredEntryUpdateRequest request) {
        StructuredEntry updated = new StructuredEntry();
        updated.setBpSystolic(request.bpSystolic());
        updated.setBpDiastolic(request.bpDiastolic());
        updated.setHeartRate(request.heartRate());
        updated.setTemperature(request.temperature());
        updated.setSo2(request.so2());
        updated.setPainLevel(request.painLevel());
        updated.setMedsGiven(request.medsGiven());
        updated.setBehavior(request.behavior());
        updated.setInterventions(request.interventions());
        updated.setEvaluation(request.evaluation());

        StructuredEntry result = structuredEntryService.updateEntry(id, updated);
        return mapToResponse(result);
    }

    @PostMapping("/{id}/verify")
    @PreAuthorize("hasAnyRole('ADMIN', 'NURSE')")
    public StructuredEntryResponse verifyEntry(@PathVariable Long id, Authentication authentication) {
        StructuredEntry result = structuredEntryService.verifyEntry(id, authentication.getName());
        return mapToResponse(result);
    }

    private StructuredEntryResponse mapToResponse(StructuredEntry entry) {
        return new StructuredEntryResponse(
                entry.getId(),
                entry.getPatientId(),
                entry.getOriginalNoteId(),
                entry.getBpSystolic(),
                entry.getBpDiastolic(),
                entry.getHeartRate(),
                entry.getTemperature(),
                entry.getSo2(),
                entry.getPainLevel(),
                entry.getMedsGiven(),
                entry.getBehavior(),
                entry.getInterventions(),
                entry.getEvaluation(),
                entry.isVerified(),
                entry.getVerifiedByUsername(),
                entry.getVerifiedAt(),
                entry.getLastEditedAt());
    }
}
