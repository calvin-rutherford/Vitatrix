package com.shiftpilot.backend.service;

import com.shiftpilot.backend.model.NursingNote;

import com.shiftpilot.backend.repository.NursingNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NursingNoteService {

    private final NursingNoteRepository nursingNoteRepository;
    private final StructuredEntryService structuredEntryService;

    @Transactional
    public NursingNote createNote(NursingNote note) {
        NursingNote savedNote = nursingNoteRepository.save(note);
        // Trigger deterministic parser
        structuredEntryService.createFromNote(savedNote);
        return savedNote;
    }
}
