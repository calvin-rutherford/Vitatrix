package com.shiftpilot.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "nursing_notes")
@Data
@NoArgsConstructor
public class NursingNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String nurseUsername;

    @Column(nullable = false)
    private Long patientId; // Loose coupling to allow for easier potential microservice split later if
                            // needed, but direct FK is also fine. Keeping it simple as requested.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Shift shift;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public enum Shift {
        DAY, EVENING, NIGHT
    }
}
