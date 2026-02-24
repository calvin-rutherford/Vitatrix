<<<<<<< HEAD
# Vitatrix Sprint 2B

Vitatrix is a backend service for nursing charting and shift handoff summaries.
This repository contains the complete Sprint 2B codebase, building upon Sprint 2A with stateless JWT authentication, role-based authorization, and dynamic shift summaries.

## What Sprint 2B Added/Changed
- **JWT Authentication**: Replaced Basic Auth with stateless JWT tokens via `/api/auth/login`.
- **Role-Based Authorization**: Methods and endpoints are now secured by `ADMIN` and `NURSE` roles.
- **Auto-Attribution**: Notes are automatically attributed to the authenticated nurse creating them, rather than relying on client input.
- **Dynamic Shift Summaries**: `/api/shift-summaries/generate` and `/api/shift-summaries/latest` now support dynamic filtering by `shift`, `patientId`, `nurseUsername`, `from`, and `to`. Roles restrict nurses to only query their own summaries.
- **User Management**: Admins can now create and list users via `/api/admin/users`.
- **Validation Cleanup**: Hardened DTO boundaries and validation rules.

## Prerequisites

- Java 17+
- Docker & Docker Compose
- Maven

## Running the Application

1. **Start PostgreSQL Database**
   ```bash
   docker compose up -d
   ```

2. **Environment Variables**
   The application uses the `JWT_SECRET` and `JWT_EXP_SECONDS` environment variables. In the `dev` profile, these have defaults. For production, you must set them securely:
   ```bash
   export JWT_SECRET=yourSuperSecretKeyThatIsAtLeast256BitsLong
   export JWT_EXP_SECONDS=3600
   ```

3. **Run the Spring Boot Application**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

4. **Access Swagger UI**
   Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) to explore the API.
   - To use secured endpoints via Swagger, first use `/api/auth/login` to get a token.
   - Click the **"Authorize"** button at the top of Swagger.
   - Enter `Bearer <token>` in the value field and authenticate.

## Default Credentials (Dev Profile)

| Role | Username | Password |
|------|----------|----------|
| ADMIN| admin    | admin123 |
| NURSE| nurse1   | nurse123 |

## Curl Examples

### 1. Login (Get JWT Token)
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}' | jq -r .token)
echo $TOKEN
```

### 2. Create Patient (Admin Only)
```bash
curl -X POST http://localhost:8080/api/patients \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Alice",
    "lastName": "Johnson",
    "roomNumber": "103",
    "admissionDate": "2023-10-27",
    "diagnosis": "Flu"
  }'
```

### 3. List Patients (Admin/Nurse)
```bash
curl -X GET http://localhost:8080/api/patients \
  -H "Authorization: Bearer $TOKEN"
```

### 4. Create a Note (Nurse/Admin)
*Note: The system automatically assigns the creating user to the `nurseUsername` field.*
```bash
# Obtain a nurse token first
NURSE_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login -d '{"username": "nurse1", "password": "nurse123"}' -H "Content-Type: application/json" | jq -r .token)

curl -X POST http://localhost:8080/api/notes \
  -H "Authorization: Bearer $NURSE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 1,
    "content": "Patient reports pain at 6/10. Administered meds. Shift: DAY",
    "shift": "DAY"
  }'
```

### 5. Patch Structured Entry
```bash
# Use entry ID returned from note creation or found via GET endpoints
curl -X PATCH http://localhost:8080/api/structured-entries/1 \
  -H "Authorization: Bearer $NURSE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "painLevel": 4,
    "behavior": "Calm"
  }'
```

### 6. Verify Structured Entry
```bash
curl -X POST http://localhost:8080/api/structured-entries/1/verify \
  -H "Authorization: Bearer $NURSE_TOKEN"
```

### 7. Generate Shift Summary with Filters
```bash
curl -X POST "http://localhost:8080/api/shift-summaries/generate?shift=DAY&nurseUsername=nurse1" \
  -H "Authorization: Bearer $NURSE_TOKEN"
```

### 8. Get Latest Shift Summary
```bash
curl -X GET "http://localhost:8080/api/shift-summaries/latest?shift=DAY&nurseUsername=nurse1" \
  -H "Authorization: Bearer $NURSE_TOKEN"
```

### 9. Create User (Admin Only)
```bash
curl -X POST http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nurse2",
    "password": "password123",
    "role": "NURSE",
    "facilityName": "General Hospital"
  }'
```

### 10. List Users (Admin Only)
```bash
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer $TOKEN"
```

## Running Tests
```bash
mvn test
```

## Smoke Test Checklist
- [ ] **Infrastructure**: `docker compose up -d` starts PostgreSQL properly.
- [ ] **Application**: `mvn spring-boot:run -Dspring-boot.run.profiles=dev` starts successfully without errors.
- [ ] **Auth**: Swagger UI is accessible; login works for `admin` and `nurse1`; invalid passwords return HTTP 403/401.
- [ ] **Permissions**: `nurse1` receives a 403 Forbidden when trying to create a patient or a user.
- [ ] **Attribution**: Notes created by `nurse1` have `nurseUsername` automatically set to `nurse1`.
- [ ] **Filtering**: Shift Summary generation respects the dynamic query filters (e.g. `shift`, `nurseUsername`).

## Migration Note
This version replaces Basic Auth with JWT. All database tables and columns are compatible with Sprint 2A, but ensure you migrate logic that passes `nurseUsername` from your clients, as that parameter is no longer accepted in `NoteRequest`.
=======
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


>>>>>>> 342939fb29637d9b4d174651e7c9dd5996996f0c
