package com.shiftpilot.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shiftpilot.backend.dto.StructuredEntryUpdateRequest;
import com.shiftpilot.backend.model.StructuredEntry;
import com.shiftpilot.backend.service.StructuredEntryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StructuredEntryControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private StructuredEntryService structuredEntryService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @WithMockUser(username = "nurse1", roles = "NURSE")
        public void testPatchEntry_ValidatesPainRange() throws Exception {
                // Use case: "Validate pain 0–10".
                // The service should allow valid updates.
                // Note: The previous logic was testing successful update.
                // Prompt says: "StructuredEntry PATCH validates painLevel 0–10 (400)"

                StructuredEntryUpdateRequest request = new StructuredEntryUpdateRequest(
                                null, null, null, null, null, 11, null, null, null, null);

                mockMvc.perform(patch("/api/structured-entries/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "nurse1", roles = "NURSE")
        public void testVerifyEntry_SetsVerifiedFields() throws Exception {
                StructuredEntry mockEntry = new StructuredEntry();
                mockEntry.setVerified(true);
                mockEntry.setVerifiedByUsername("nurse1");
                mockEntry.setVerifiedAt(LocalDateTime.now());

                when(structuredEntryService.verifyEntry(eq(1L), eq("nurse1")))
                                .thenReturn(mockEntry);

                mockMvc.perform(post("/api/structured-entries/1/verify"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.isVerified").value(true))
                                .andExpect(jsonPath("$.verifiedByUsername").value("nurse1"));

                verify(structuredEntryService).verifyEntry(eq(1L), eq("nurse1"));
        }
}
