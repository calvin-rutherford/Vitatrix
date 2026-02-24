package com.vitatrix.backend.controller;

import com.vitatrix.backend.dto.PatientRequest;
import com.vitatrix.backend.dto.PatientResponse;
import com.vitatrix.backend.model.Patient;
import com.vitatrix.backend.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PatientResponse createPatient(@RequestBody PatientRequest request) {
        Patient patient = new Patient();
        patient.setFirstName(request.firstName());
        patient.setLastName(request.lastName());
        patient.setRoomNumber(request.roomNumber());
        patient.setAdmissionDate(request.admissionDate());
        patient.setDiagnosis(request.diagnosis());

        Patient saved = patientService.createPatient(patient);
        return mapToResponse(saved);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NURSE')")
    public List<PatientResponse> getAllPatients() {
        return patientService.getAllPatients().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'NURSE')")
    public PatientResponse getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
    }

    private PatientResponse mapToResponse(Patient p) {
        return new PatientResponse(
                p.getId(),
                p.getFirstName(),
                p.getLastName(),
                p.getRoomNumber(),
                p.getAdmissionDate(),
                p.getDiagnosis());
    }
}
