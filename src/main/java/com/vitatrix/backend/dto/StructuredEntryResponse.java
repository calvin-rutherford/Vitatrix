package com.vitatrix.backend.dto;

import java.time.LocalDateTime;

public record StructuredEntryResponse(
        Long id,
        Long patientId,
        Long originalNoteId,
        Integer bpSystolic,
        Integer bpDiastolic,
        Integer heartRate,
        Double temperature,
        Integer so2,
        Integer painLevel,
        String medsGiven,
        String behavior,
        String interventions,
        String evaluation,
        boolean isVerified,
        String verifiedByUsername,
        LocalDateTime verifiedAt,
        LocalDateTime lastEditedAt) {
}
