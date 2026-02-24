package com.vitatrix.backend.dto;

import java.time.LocalDate;

public record PatientResponse(
        Long id,
        String firstName,
        String lastName,
        String roomNumber,
        LocalDate admissionDate,
        String diagnosis) {
}
