package com.shiftpilot.backend.dto;

import com.shiftpilot.backend.model.NursingNote;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NoteRequest(
                @NotNull(message = "Patient ID is required") Long patientId,
                @NotBlank(message = "Content is required") String content,
                @NotNull(message = "Shift is required") NursingNote.Shift shift) {
}
