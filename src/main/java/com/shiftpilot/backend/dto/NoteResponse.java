package com.shiftpilot.backend.dto;

import com.shiftpilot.backend.model.NursingNote;
import java.time.LocalDateTime;

public record NoteResponse(
        Long id,
        Long patientId,
        String nurseUsername,
        String content,
        LocalDateTime timestamp,
        NursingNote.Shift shift) {
}
