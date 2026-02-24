package com.vitatrix.backend.service;

import com.vitatrix.backend.model.NursingNote;
import com.vitatrix.backend.model.StructuredEntry;
import com.vitatrix.backend.repository.StructuredEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class StructuredEntryService {

    private final StructuredEntryRepository structuredEntryRepository;

    @Transactional
    public void createFromNote(NursingNote note) {
        StructuredEntry entry = new StructuredEntry();
        entry.setPatientId(note.getPatientId());
        entry.setOriginalNoteId(note.getId());

        parseContent(note.getContent(), entry);

        structuredEntryRepository.save(entry);
    }

    private void parseContent(String content, StructuredEntry entry) {
        String lowerContent = content.toLowerCase();

        // BP Parser: "BP 138/90" or "BP 138 over 90"
        Pattern bpPattern = Pattern.compile("bp\\s+(\\d{2,3})[\\/\\s]+(?:over\\s+)?(\\d{2,3})");
        Matcher bpMatcher = bpPattern.matcher(lowerContent);
        if (bpMatcher.find()) {
            entry.setBpSystolic(Integer.parseInt(bpMatcher.group(1)));
            entry.setBpDiastolic(Integer.parseInt(bpMatcher.group(2)));
        }

        // Pain Parser: "Pain 6" or "Pain 6/10"
        Pattern painPattern = Pattern.compile("pain\\s+(\\d{1,2})");
        Matcher painMatcher = painPattern.matcher(lowerContent);
        if (painMatcher.find()) {
            int pain = Integer.parseInt(painMatcher.group(1));
            // Basic validation for parser
            if (pain >= 0 && pain <= 10) {
                entry.setPainLevel(pain);
            }
        }

        // Temp Parser: "Temp 100.2"
        Pattern tempPattern = Pattern.compile("temp\\s+(\\d{2,3}(?:\\.\\d)?)");
        Matcher tempMatcher = tempPattern.matcher(lowerContent);
        if (tempMatcher.find()) {
            entry.setTemperature(Double.parseDouble(tempMatcher.group(1)));
        }

        // Meds
        if (lowerContent.contains("tylenol")) {
            appendMed(entry, "Tylenol");
        }
        if (lowerContent.contains("morphine")) {
            appendMed(entry, "Morphine");
        }

        // Behavior
        if (lowerContent.contains("restless")) {
            entry.setBehavior("Restless");
        } else if (lowerContent.contains("agitated")) {
            entry.setBehavior("Agitated");
        } else if (lowerContent.contains("calm")) {
            entry.setBehavior("Calm");
        }
    }

    private void appendMed(StructuredEntry entry, String med) {
        if (entry.getMedsGiven() == null || entry.getMedsGiven().isEmpty()) {
            entry.setMedsGiven(med);
        } else {
            entry.setMedsGiven(entry.getMedsGiven() + ", " + med);
        }
    }

    public List<StructuredEntry> getEntriesByPatientId(Long patientId) {
        return structuredEntryRepository.findByPatientId(patientId);
    }

    @Transactional
    public StructuredEntry updateEntry(Long id, StructuredEntry updates) {
        StructuredEntry entry = structuredEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        if (updates.getPainLevel() != null)
            entry.setPainLevel(updates.getPainLevel());
        if (updates.getBpSystolic() != null)
            entry.setBpSystolic(updates.getBpSystolic());
        if (updates.getBpDiastolic() != null)
            entry.setBpDiastolic(updates.getBpDiastolic());
        if (updates.getMedsGiven() != null)
            entry.setMedsGiven(updates.getMedsGiven());
        if (updates.getBehavior() != null)
            entry.setBehavior(updates.getBehavior());
        if (updates.getInterventions() != null)
            entry.setInterventions(updates.getInterventions());
        if (updates.getEvaluation() != null)
            entry.setEvaluation(updates.getEvaluation());

        entry.setLastEditedAt(LocalDateTime.now());

        // If modified, unverify
        entry.setVerified(false);
        entry.setVerifiedByUsername(null);
        entry.setVerifiedAt(null);

        return structuredEntryRepository.save(entry);
    }

    @Transactional
    public StructuredEntry verifyEntry(Long id, String username) {
        StructuredEntry entry = structuredEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        entry.setVerified(true);
        entry.setVerifiedByUsername(username);
        entry.setVerifiedAt(LocalDateTime.now());

        return structuredEntryRepository.save(entry);
    }
}
