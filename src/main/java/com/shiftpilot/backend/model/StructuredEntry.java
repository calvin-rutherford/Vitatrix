package com.shiftpilot.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "structured_entries")
@Data
@NoArgsConstructor
public class StructuredEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long originalNoteId;

    // Vitals
    private Integer bpSystolic;
    private Integer bpDiastolic;
    private Integer heartRate;
    private Double temperature;
    private Integer so2;

    // Assessment
    private Integer painLevel; // 0-10

    @Column(columnDefinition = "TEXT")
    private String medsGiven;

    @Column(columnDefinition = "TEXT")
    private String behavior; // restless, agitated, calm

    @Column(columnDefinition = "TEXT")
    private String interventions;

    @Column(columnDefinition = "TEXT")
    private String evaluation;

    // Verification (Trust Features)
    private boolean isVerified;
    private String verifiedByUsername;
    private LocalDateTime verifiedAt;
    private LocalDateTime lastEditedAt;

}
