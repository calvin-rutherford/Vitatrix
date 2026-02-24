package com.shiftpilot.backend.dto;

import com.shiftpilot.backend.model.NursingNote;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ShiftSummaryResponse(
        Long id,
        NursingNote.Shift shift,
        LocalDate date,
        LocalDateTime generatedAt,
        String content) {
}
