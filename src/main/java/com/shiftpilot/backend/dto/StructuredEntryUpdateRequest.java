package com.shiftpilot.backend.dto;

public record StructuredEntryUpdateRequest(
        Integer bpSystolic,
        Integer bpDiastolic,
        Integer heartRate,
        Double temperature,
        Integer so2,
        Integer painLevel,
        String medsGiven,
        String behavior,
        String interventions,
        String evaluation) {
}
