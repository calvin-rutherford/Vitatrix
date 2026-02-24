package com.vitatrix.backend.controller;

import com.vitatrix.backend.dto.NoteRequest;
import com.vitatrix.backend.dto.NoteResponse;
import com.vitatrix.backend.model.NursingNote;
import com.vitatrix.backend.service.NursingNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NursingNoteController {

    private final NursingNoteService nursingNoteService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NURSE')")
    public NoteResponse createNote(@RequestBody NoteRequest request, Authentication authentication) {
        NursingNote note = new NursingNote();
        note.setPatientId(request.patientId());
        note.setContent(request.content());
        note.setShift(request.shift());
        note.setNurseUsername(authentication.getName());

        NursingNote saved = nursingNoteService.createNote(note);

        return new NoteResponse(
                saved.getId(),
                saved.getPatientId(),
                saved.getNurseUsername(),
                saved.getContent(),
                saved.getTimestamp(),
                saved.getShift());
    }
}
