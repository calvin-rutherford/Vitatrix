package com.vitatrix.backend.config;

import com.vitatrix.backend.model.NursingNote;
import com.vitatrix.backend.model.NursingNote.Shift;
import com.vitatrix.backend.model.Patient;
import com.vitatrix.backend.model.UserAccount;
import com.vitatrix.backend.repository.NursingNoteRepository;
import com.vitatrix.backend.repository.PatientRepository;
import com.vitatrix.backend.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@Profile("dev")
public class DataSeeder {

    private final UserAccountRepository userAccountRepository;
    private final PatientRepository patientRepository;
    private final NursingNoteRepository nursingNoteRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Seed Users
            if (userAccountRepository.count() == 0) {
                UserAccount admin = new UserAccount();
                admin.setUsername("admin");
                admin.setPasswordHash(passwordEncoder.encode("admin123"));
                admin.setRole("ADMIN");
                admin.setFacilityName("Central Hospital");
                userAccountRepository.save(admin);

                UserAccount nurse1 = new UserAccount();
                nurse1.setUsername("nurse1");
                nurse1.setPasswordHash(passwordEncoder.encode("nurse123"));
                nurse1.setRole("NURSE");
                nurse1.setFacilityName("Central Hospital");
                userAccountRepository.save(nurse1);
            }

            // Seed Patients
            if (patientRepository.count() == 0) {
                Patient p1 = new Patient();
                p1.setFirstName("John");
                p1.setLastName("Doe");
                p1.setRoomNumber("101");
                p1.setAdmissionDate(LocalDate.now().minusDays(5));
                p1.setDiagnosis("Pneumonia");
                patientRepository.save(p1);

                Patient p2 = new Patient();
                p2.setFirstName("Jane");
                p2.setLastName("Smith");
                p2.setRoomNumber("102");
                p2.setAdmissionDate(LocalDate.now().minusDays(2));
                p2.setDiagnosis("Hypertension");
                patientRepository.save(p2);

                // Seed Notes
                NursingNote n1 = new NursingNote();
                n1.setPatientId(p1.getId());
                n1.setNurseUsername("nurse1");
                n1.setContent("Patient resting comfortably. BP 120/80. Pain 2/10.");
                n1.setTimestamp(LocalDateTime.now().minusHours(4));
                n1.setShift(NursingNote.Shift.DAY);
                nursingNoteRepository.save(n1);

                NursingNote n2 = new NursingNote();
                n2.setPatientId(p1.getId());
                n2.setNurseUsername("nurse1");
                n2.setContent("Administered Tylenol for fever. Temp 100.2.");
                n2.setTimestamp(LocalDateTime.now().minusHours(2));
                n2.setShift(NursingNote.Shift.EVENING);
                nursingNoteRepository.save(n2);

                NursingNote n3 = new NursingNote();
                n3.setPatientId(p2.getId());
                n3.setNurseUsername("nurse1");
                n3.setContent("Patient complains of headache. BP 145/95. Restless behavior observed.");
                n3.setTimestamp(LocalDateTime.now().minusHours(1));
                n3.setShift(NursingNote.Shift.NIGHT);
                nursingNoteRepository.save(n3);
            }
        };
    }
}
