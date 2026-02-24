package com.shiftpilot.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record PatientRequest(
                @NotBlank(message = "First name is required") String firstName,
                @NotBlank(message = "Last name is required") String lastName,
                @NotBlank(message = "Room number is required") String roomNumber,
                @NotNull(message = "Admission date is required") LocalDate admissionDate,
                @NotBlank(message = "Diagnosis is required") String diagnosis) {
}
