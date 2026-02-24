package com.vitatrix.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    // Used only for creation, won't be sent in response
    private String password;

    @NotBlank(message = "Role is required")
    private String role;

    private String facilityName;

    private LocalDateTime createdAt;
}
