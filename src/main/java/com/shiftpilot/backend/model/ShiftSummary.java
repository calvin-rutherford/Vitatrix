package com.shiftpilot.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "shift_summaries")
@Data
@NoArgsConstructor
public class ShiftSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NursingNote.Shift shift; // DAY, EVENING, NIGHT

    @Column(nullable = false)
    private LocalDate date;

    private LocalDateTime generatedAt;

    private String filtersUsed;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }
}
