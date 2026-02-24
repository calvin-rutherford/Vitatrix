package com.vitatrix.backend.repository;

import com.vitatrix.backend.model.StructuredEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StructuredEntryRepository extends JpaRepository<StructuredEntry, Long> {
    List<StructuredEntry> findByPatientId(Long patientId);
}
