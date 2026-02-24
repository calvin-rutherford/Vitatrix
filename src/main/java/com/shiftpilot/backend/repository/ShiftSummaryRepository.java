package com.shiftpilot.backend.repository;

import com.shiftpilot.backend.model.NursingNote;
import com.shiftpilot.backend.model.ShiftSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface ShiftSummaryRepository extends JpaRepository<ShiftSummary, Long> {
    Optional<ShiftSummary> findByShiftAndDateOrderByGeneratedAtDesc(NursingNote.Shift shift, LocalDate date);
}
