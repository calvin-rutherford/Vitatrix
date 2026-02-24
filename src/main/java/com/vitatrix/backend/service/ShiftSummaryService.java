package com.vitatrix.backend.service;

import com.vitatrix.backend.model.NursingNote;
import com.vitatrix.backend.model.ShiftSummary;
import com.vitatrix.backend.model.StructuredEntry;
import com.vitatrix.backend.repository.NursingNoteRepository;
import com.vitatrix.backend.repository.ShiftSummaryRepository;
import com.vitatrix.backend.repository.StructuredEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShiftSummaryService {

    private final ShiftSummaryRepository shiftSummaryRepository;
    private final StructuredEntryRepository structuredEntryRepository;
    private final NursingNoteRepository nursingNoteRepository;

    public Optional<ShiftSummary> getLatestSummary(NursingNote.Shift shift, Long patientId, String nurseUsername,
            LocalDateTime from, LocalDateTime to) {
        String filters = createFiltersJson(shift, patientId, nurseUsername, from, to);
        return shiftSummaryRepository.findAll().stream()
                .filter(s -> s.getShift() == shift && filters.equals(s.getFiltersUsed()))
                .max((s1, s2) -> s1.getGeneratedAt().compareTo(s2.getGeneratedAt()));
    }

    @Transactional
    public ShiftSummary generateSummary(NursingNote.Shift shift, Long patientId, String nurseUsername,
            LocalDateTime from, LocalDateTime to) {
        LocalDate today = LocalDate.now();
        List<StructuredEntry> entries = structuredEntryRepository.findAll();

        List<StructuredEntry> filteredEntries = entries.stream()
                .filter(entry -> {
                    return nursingNoteRepository.findById(entry.getOriginalNoteId())
                            .map(note -> {
                                if (note.getShift() != shift)
                                    return false;
                                if (patientId != null && !patientId.equals(note.getPatientId()))
                                    return false;
                                if (nurseUsername != null && !nurseUsername.equals(note.getNurseUsername()))
                                    return false;
                                if (from != null && note.getTimestamp().isBefore(from))
                                    return false;
                                if (to != null && note.getTimestamp().isAfter(to))
                                    return false;
                                return true;
                            })
                            .orElse(false);
                })
                .collect(Collectors.toList());

        Map<Long, List<StructuredEntry>> entriesByPatient = filteredEntries.stream()
                .collect(Collectors.groupingBy(StructuredEntry::getPatientId));

        StringBuilder report = new StringBuilder();
        report.append("Shift Summary - ").append(shift).append(" - ").append(today).append("\n\n");

        if (entriesByPatient.isEmpty()) {
            report.append("No entries found for these filters.");
        } else {
            entriesByPatient.forEach((pId, patientEntries) -> {
                report.append("Patient ID: ").append(pId).append("\n");

                if (!patientEntries.isEmpty()) {
                    StructuredEntry last = patientEntries.get(patientEntries.size() - 1);
                    report.append("  Last BP: ").append(last.getBpSystolic()).append("/").append(last.getBpDiastolic())
                            .append("\n");
                    report.append("  Pain: ").append(last.getPainLevel()).append("\n");
                    if (last.getBehavior() != null)
                        report.append("  Behavior: ").append(last.getBehavior()).append("\n");
                }
                report.append("\n");
            });
        }

        ShiftSummary summary = new ShiftSummary();
        summary.setShift(shift);
        summary.setDate(today);
        summary.setContent(report.toString());
        summary.setFiltersUsed(createFiltersJson(shift, patientId, nurseUsername, from, to));

        return shiftSummaryRepository.save(summary);
    }

    private String createFiltersJson(NursingNote.Shift shift, Long patientId, String nurseUsername, LocalDateTime from,
            LocalDateTime to) {
        return String.format("{\"shift\":\"%s\",\"patientId\":%s,\"nurseUsername\":%s,\"from\":%s,\"to\":%s}",
                shift,
                patientId == null ? "null" : patientId,
                nurseUsername == null ? "null" : "\"" + nurseUsername + "\"",
                from == null ? "null" : "\"" + from + "\"",
                to == null ? "null" : "\"" + to + "\"");
    }
}
