package com.shiftpilot.backend.repository;

import com.shiftpilot.backend.model.NursingNote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NursingNoteRepository extends JpaRepository<NursingNote, Long> {
    List<NursingNote> findByPatientId(Long patientId);
}
