package com.vitatrix.backend.repository;

import com.vitatrix.backend.model.NursingNote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NursingNoteRepository extends JpaRepository<NursingNote, Long> {
    List<NursingNote> findByPatientId(Long patientId);
}
