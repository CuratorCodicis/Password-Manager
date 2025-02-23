package passwordmanager.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for outgoing password responses.
 * This object send back the details of the password entry, including the decrypted password.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResponseDTO {
    private Long id;
    private String username;
    private String plaintextPassword;
    private String service;
    private String description;
    private LocalDateTime createdAt;
}
