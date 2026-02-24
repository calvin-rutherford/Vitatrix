package com.vitatrix.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitatrix.backend.dto.AuthRequest;
import com.vitatrix.backend.dto.NoteRequest;
import com.vitatrix.backend.dto.PatientRequest;
import com.vitatrix.backend.model.NursingNote;
import com.vitatrix.backend.repository.NursingNoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev") // Uses DataSeeder
public class Sprint2BSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NursingNoteRepository nursingNoteRepository;

    @Test
    public void testAuthLoginReturnsToken() throws Exception {
        AuthRequest loginRequest = new AuthRequest("admin", "admin123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser(username = "nurse_test_user", roles = { "NURSE" })
    public void testNoteCreationAutoAttribution() throws Exception {
        NoteRequest noteRequest = new NoteRequest(1L, "Patient seems stable.", NursingNote.Shift.DAY);

        mockMvc.perform(post("/api/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(noteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nurseUsername").value("nurse_test_user"));
    }

    @Test
    @WithMockUser(username = "nurse1", roles = { "NURSE" })
    public void testNurseCannotCreatePatient() throws Exception {
        PatientRequest patientRequest = new PatientRequest("John", "Doe", "101", LocalDate.now(), "Flu");

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patientRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    public void testShiftSummaryFiltering() throws Exception {
        // Generating summary for EVENING shift, should not fail and return summary
        mockMvc.perform(post("/api/shift-summaries/generate")
                .param("shift", "EVENING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shift").value("EVENING"));
    }
}
