package com.shiftpilot.backend.repository;

import com.shiftpilot.backend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
