package passwordmanager.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for incoming password creation requests.
 * The client provides the plaintext password here, along with other details.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordRequestDTO {
    //@NotBlank(message = "Username is required")
    private String username;

    private String plaintextPassword;

    private String service;

    private String description;
}
