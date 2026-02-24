# Vitatrix

Vitatrix is a backend service for nursing charting and shift handoff summaries.
This repository contains the complete Sprint 2A codebase.

## Prerequisites

- Java 17+
- Docker & Docker Compose
- Maven

## file Tree

```
src/main/java/com/shiftpilot/backend
├── ShiftPilotBackendApplication.java
├── config
│   ├── DataSeeder.java
│   └── SecurityConfig.java
├── controller
│   ├── NursingNoteController.java
│   ├── PatientController.java
│   ├── ShiftSummaryController.java
│   └── StructuredEntryController.java
├── dto
│   ├── NoteDto.java
│   ├── PatientDto.java
│   ├── ShiftSummaryDto.java
│   └── StructuredEntryDto.java
├── exception
│   └── GlobalExceptionHandler.java
├── model
│   ├── NursingNote.java
│   ├── Patient.java
│   ├── ShiftSummary.java
│   ├── StructuredEntry.java
│   └── UserAccount.java
├── repository
│   ├── NursingNoteRepository.java
│   ├── PatientRepository.java
│   ├── ShiftSummaryRepository.java
│   ├── StructuredEntryRepository.java
│   └── UserAccountRepository.java
├── security
│   └── CustomUserDetailsService.java
└── service
    ├── NursingNoteService.java
    ├── PatientService.java
    ├── ShiftSummaryService.java
    └── StructuredEntryService.java
```

## Running the Application

1. **Start PostgreSQL Database**
   ```bash
   docker compose up -d
   ```

2. **Run the Spring Boot Application**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

3. **Access Swagger UI**
   Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) to explore the API.

## Default Credentials

| Role | Username | Password |
|------|----------|----------|
| ADMIN| admin    | admin123 |
| NURSE| nurse1   | nurse123 |

## Curl Examples

### 1. Create a Note (Nurse)
```bash
curl -X POST http://localhost:8080/api/notes \
  -u nurse1:nurse123 \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 1,
    "content": "Patient complains of severe headache. BP 150/95. Pain 8/10. Given Tylenol. Shift: NIGHT",
    "shift": "NIGHT"
  }'
```

### 2. View Patients (Nurse)
```bash
curl -X GET http://localhost:8080/api/patients \
  -u nurse1:nurse123
```

### 3. Create Patient (Admin Only)
```bash
curl -X POST http://localhost:8080/api/patients \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Alice",
    "lastName": "Johnson",
    "roomNumber": "103",
    "admissionDate": "2023-10-27",
    "diagnosis": "Flu"
  }'
```

### 4. View Structured Entries for Patient (Nurse)
```bash
curl -X GET http://localhost:8080/api/structured-entries/patient/1 \
  -u nurse1:nurse123
```

### 5. Verify Structured Entry (Nurse)
```bash
# Replace {id} with actual entry ID from previous step
curl -X POST http://localhost:8080/api/structured-entries/{id}/verify \
  -u nurse1:nurse123
```

### 6. Update Structured Entry (Nurse)
```bash
# Replace {id} with actual entry ID
curl -X PATCH http://localhost:8080/api/structured-entries/{id} \
  -u nurse1:nurse123 \
  -H "Content-Type: application/json" \
  -d '{
    "painLevel": 7,
    "bpSystolic": 145
  }'
```

### 7. Generate Shift Summary (Nurse/Admin)
```bash
curl -X POST "http://localhost:8080/api/shift-summaries?shift=DAY" \
  -u nurse1:nurse123
```

### 8. Get Latest Shift Summary
```bash
curl -X GET "http://localhost:8080/api/shift-summaries/latest?shift=DAY" \
  -u nurse1:nurse123
```

## Running Tests
```bash
mvn test
```

## Migration Note
This version fully replaces the Sprint 1 H2 database with PostgreSQL. Make sure to stop any previous instances and run `docker compose up` to initialize the fresh database.
The `NursingNote` table now includes a `shift` column (DAY, EVENING, NIGHT). Existing data in `dev` may be incompatible if not dropped.


