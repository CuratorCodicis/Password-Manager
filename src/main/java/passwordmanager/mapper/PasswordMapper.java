package passwordmanager.mapper;

import passwordmanager.dto.PasswordRequestDTO;
import passwordmanager.dto.PasswordResponseDTO;
import passwordmanager.model.Password;

/**
 * Mapper class to convert Password entities and DTOs.
 */
public class PasswordMapper {
    /**
     * Converts a PasswordRequestDTO to a Password entity.
     * Note: The entity's encryptedPassword is not set here. It must be set at the service-level after encrypting the plaintext.
     */
    public static Password toEntity(PasswordRequestDTO dto) {
        Password password = new Password();
        password.setUsername(dto.getUsername());
        password.setService(dto.getService());
        password.setDescription(dto.getDescription());
        return password;
    }

    /**
     * Converts a Password entity to a PasswordResponseDTO.
     * The plaintext password is decrypted at the service-level and provided as a parameter.
     */
    public static PasswordResponseDTO toDTO(Password password, String plaintextPassword) {
        PasswordResponseDTO dto = new PasswordResponseDTO();
        dto.setId(password.getId());
        dto.setUsername(password.getUsername());
        dto.setService(password.getService());
        dto.setDescription(password.getDescription());
        dto.setCreatedAt(password.getCreatedAt());
        dto.setPlaintextPassword(plaintextPassword);
        return dto;
    }
}
