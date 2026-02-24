package com.shiftpilot.backend.repository;

import com.shiftpilot.backend.model.StructuredEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StructuredEntryRepository extends JpaRepository<StructuredEntry, Long> {
    List<StructuredEntry> findByPatientId(Long patientId);
}
