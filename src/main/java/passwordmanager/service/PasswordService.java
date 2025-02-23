package passwordmanager.service;

import passwordmanager.dto.PasswordRequestDTO;
import passwordmanager.dto.PasswordResponseDTO;
import passwordmanager.exception.DuplicateResourceException;
import passwordmanager.exception.ResourceNotFoundException;
import passwordmanager.exception.NoPasswordProvidedException;
import passwordmanager.mapper.PasswordMapper;
import passwordmanager.model.Password;
import passwordmanager.repository.PasswordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing password entries.
 * This class handles the business logic, such as introducing encryption before saving
 * and decryption after retrieving.
 */
@Service
@Transactional // Methods are to be executed within a transaction, see ACID
@RequiredArgsConstructor // Generate constructor for all final fields. If only one constructor Spring automatically will use it for dependency injection without @Autowired
public class PasswordService {

    private final PasswordRepository passwordRepository;
    private final EncryptionService encryptionService;

    //------------
    // DTO methods
    // -----------

    /**
     * Create a new password entry.
     * 1. Converts the incoming DTO to a Password entity.
     * 2. Encrypts the plaintext password from the DTO.
     * 3. Saves the entity.
     * 4. Converts the saved entity to a Response DTO (decrypting the stored password for demonstration).
     *
     * @param request the data from the client.
     * @return a response DTO with the saved password entry.
     */
    public PasswordResponseDTO createPassword(PasswordRequestDTO request) {
        // Check for missing password and throw an exception if necessary.
        if (request.getPlaintextPassword() == null || request.getPlaintextPassword().isBlank()) {
            throw new NoPasswordProvidedException("Plaintext password must be provided for a new password entry.");
        }

        // Check for duplication: same username (case-insensitive) and same service
        boolean duplicateExists = passwordRepository.findByUsername(request.getUsername())
                .stream()
                .anyMatch(password -> password.getService().equalsIgnoreCase(request.getService()));
        if (duplicateExists) {
            throw new DuplicateResourceException(
                    "A password for username '" + request.getUsername() +
                    "' and service '" + request.getService() + "' already exists."
            );
        }

        // If no duplicate exists, continue with creation.
        Password password = PasswordMapper.toEntity(request);
        byte[] encrypted = encryptionService.encrypt(request.getPlaintextPassword());
        password.setEncryptedPassword(encrypted);
        Password saved = passwordRepository.save(password);

        // Decrypt password again for response.
        String decrypted = encryptionService.decrypt(saved.getEncryptedPassword());
        return PasswordMapper.toDTO(saved, decrypted);
    }

    /**
     * Retrieves a single password entry by ID and returns it as a DTO
     *
     * @param id the unique identifier.
     * @return the PasswordResponseDTO containing the entry's data.
     */
    public PasswordResponseDTO getPasswordByIdDTO(Long id) {
        Password password = passwordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Password not found with id: " + id));
        String decrypted = encryptionService.decrypt(password.getEncryptedPassword());
        return PasswordMapper.toDTO(password, decrypted);
    }

    /**
     * Retrieves all password entries and returns a list of DTOs
     *
     * @return a list of PasswordResponseDTO objects.
     */
    public List<PasswordResponseDTO> getAllPasswordsDTO() {
        List<Password> passwords = passwordRepository.findAll();
        return passwords.stream().map(password -> {
            String decrypted = encryptionService.decrypt(password.getEncryptedPassword());
            return PasswordMapper.toDTO(password, decrypted);
        }).collect(Collectors.toList());
    }

    /**
     * Updates an existing password entry with data from a DTO
     *
     * @param id the ID of the entry to update.
     * @param request the update data.
     * @return the updated PasswordResponseDTO.
     */
    public PasswordResponseDTO updatePasswordDTO(Long id, PasswordRequestDTO request) {
        Password existing = passwordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Password not found with id: " + id));

        // Check for duplication: exclude the current record from the search.
        boolean duplicateExists = passwordRepository.findByUsername(request.getUsername())
                .stream()
                .filter(password -> !password.getId().equals(id))
                .anyMatch(password -> password.getService().equalsIgnoreCase(request.getService()));
        if (duplicateExists) {
            throw new DuplicateResourceException(
                    "Another password for username '" + request.getUsername() +
                    "' and service '" + request.getService() + "' already exists."
            );
        }

        // Update the basic field
        existing.setUsername(request.getUsername());
        existing.setService(request.getService());
        existing.setDescription(request.getDescription());

        // If a new plaintext password is provided, encrypt it and update the entry.
        if (request.getPlaintextPassword() != null && !request.getPlaintextPassword().isEmpty()) {
            byte[] encrypted = encryptionService.encrypt(request.getPlaintextPassword());
            existing.setEncryptedPassword(encrypted);
        }

        Password updated = passwordRepository.save(existing);
        String decrypted = encryptionService.decrypt(updated.getEncryptedPassword());
        return PasswordMapper.toDTO(updated, decrypted);
    }

    /**
     * Delete a password entry by its ID.
     *
     * @param id the unique identifier of the password entry to delete.
     */
    public void deletePassword(Long id) {
        if (!passwordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Password not found with id: " + id);
        }
        passwordRepository.deleteById(id);
    }

    /**
     * Retrieve passwords by exact username match.
     *
     * @param username the username to match search for.
     * @return a list of DTOs that matched with the given username.
     */
    public List<PasswordResponseDTO> getPasswordsByUsernameDTO(String username) {
        return passwordRepository.findByUsername(username)
                .stream()
                .map(password -> PasswordMapper.toDTO(password, encryptionService.decrypt(password.getEncryptedPassword())))
                .collect(Collectors.toList());
    }

    /**
     * Retrieve passwords by exact service match.
     *
     * @param service the username to search for.
     * @return a list of DTOs that matched with the given service.
     */
    public List<PasswordResponseDTO> getPasswordsByServiceDTO(String service) {
        return passwordRepository.findByService(service)
                .stream()
                .map(password -> PasswordMapper.toDTO(password, encryptionService.decrypt(password.getEncryptedPassword())))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of password entries where the username matches the given pattern.
     * Note: The caller must include SQL wildcards (e.g., "%pattern%") in the parameter.
     *
     * @param usernamePattern the username to search for.
     * @return a list of PasswordResponseDTO objects matching the pattern.
     * @throws ResourceNotFoundException if no entries match the given pattern.
     */
    public List<PasswordResponseDTO> getPasswordsByUsernameLikeDTO(String usernamePattern) {
        List<Password> results = passwordRepository.findByUsernameLike(usernamePattern);
        // Check if no results are found.
        if (results.isEmpty()) {
            throw new ResourceNotFoundException("No password entries found matching username pattern: " + usernamePattern);
        }
        return results.stream()
                .map(password -> {
                    // Decrypt the stored encrypted password
                    String decrypted = encryptionService.decrypt(password.getEncryptedPassword());
                    // Convert the Password entity to a DTO
                    return PasswordMapper.toDTO(password, decrypted);
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of password entries where the service matches the given pattern.
     * Note: The caller must include SQL wildcards (e.g., "%pattern%") in the parameter.
     *
     * @param servicePattern the username to search for.
     * @return a list of PasswordResponseDTO objects matching the pattern.
     * @throws  ResourceNotFoundException if no entries match the given pattern.
     */
    public List<PasswordResponseDTO> getPasswordsByServiceLikeDTO(String servicePattern) {
        List<Password> results = passwordRepository.findByServiceLike(servicePattern);
        // Check if no results are found.
        if (results.isEmpty()) {
            throw new ResourceNotFoundException("No password entries found matching service pattern: " + servicePattern);
        }
        return results.stream()
                .map(password -> {
                    // Decrypt the stored encrypted password
                    String decrypted = encryptionService.decrypt(password.getEncryptedPassword());
                    // Convert the Password entity to a DTO
                    return PasswordMapper.toDTO(password, decrypted);
                })
                .collect(Collectors.toList());
    }

    //-------------
    // CRUD Methods - only for internal use, work directly with Password entities.
    //-------------

    /**
     * Retrieve all entries from the database
     *
     * @return list of all Password objects.
     */
    private List<Password> getAllPasswords() {
        return passwordRepository.findAll(); // Already implemented by Spring data JPA
    }

    /**
     * Retrieve a single password entry by its ID.
     *
     * @param id the unique identifier for the password entry.
     * @return the Password object if found.
     * @throws RuntimeException if the password entry is not found.
     */
    private Password getPasswordById(Long id) {
        return passwordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Password not found with id: " + id));
    }

    /**
     * Save a new password entry.
     * Before saving, the plaintext password is encrypted by the encryptionService.
     *
     * @param password the Password object to save.
     * @return the saved Password object with its generated ID.
     */
    private Password savePassword(Password password) {
        return passwordRepository.save(password);
    }

    /**
     * Update an existing password entry.
     * Fetch the current entry, update its field, and re-encrypted the password if necessary.
     *
     * @param id the ID of the Password entry to update.
     * @param updatedPassword an object containting the updated field values.
     * @return the updated Password object.
     * @throws RuntimeException if the password entry is not found.
     */
    private Password updatePassword(Long id, Password updatedPassword) {
        // Retrieve existing password entry.
        Password existingPassword = passwordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Password not found with id: " + id));

        // Update fields
        existingPassword.setUsername(updatedPassword.getUsername());
        existingPassword.setService(updatedPassword.getService());
        existingPassword.setDescription(updatedPassword.getDescription());

        //TODO: If plaintext password is changed re-encrypt it using the encryptionService
        // existingPassword.setEncryptedPassword(encryptionService.encrypt(updatedPassword.getPlaintext()));

        // Save and return the updated entry
        return passwordRepository.save(existingPassword);
    }


    //--------------------------------------------------
    // Additional Query Methods, wrapped from Repository
    //--------------------------------------------------

    /**
     * Retrieve a list of password entries by exact username
     *
     * @param username the username to search for.
     * @return a list of Password objects with the matching username.
     */
    private List<Password> getPasswordByUsername(String username) {
        return passwordRepository.findByUsername(username);
    }

    /**
     * Retrieve a list of password entries where the username contains the given String
     * The caller should provide proper SQL wildcard characters (e.g. %username%)!
     *
     * @param usernamePattern the pattern to search for.
     * @return a list of Password objects matching the pattern.
     */
    private List<Password> getPasswordsByUsernameLike(String usernamePattern) {
        return passwordRepository.findByUsernameLike(usernamePattern);
    }

    /**
     * Retrieve a list of password entries by exact service.
     *
     * @param service the service name to search for.
     * @return a list of Password objects with the matching service.
     */
    private List<Password> getPasswordsByService(String service) {
        return passwordRepository.findByService(service);
    }

    /**
     * Retrieve a list of password entries where the service name is similar to the provided pattern.
     * Note: The caller should provide proper SQL wildcard characters (e.g., %pattern%).
     *
     * @param servicePattern the pattern to search for.
     * @return a list of Password objects matching the service pattern.
     */
    private List<Password> getPasswordsByServiceLike(String servicePattern) {
        return passwordRepository.findByServiceLike(servicePattern);
    }


}
