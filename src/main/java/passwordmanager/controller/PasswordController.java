package passwordmanager.controller;

import passwordmanager.dto.PasswordRequestDTO;
import passwordmanager.dto.PasswordResponseDTO;
import passwordmanager.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing password entries.
 * Maps HTTP endpoints to the service layer.
 */
@RestController
@RequestMapping("api/passwords")
@RequiredArgsConstructor // generates constructor for final fields
public class PasswordController {

    private final PasswordService passwordService;

    //----------------
    // POST - Create
    //----------------

    /**
     * POST /api/passwords
     * Create new password entry.
     *
     * @param request the incoming DTO containing plaintext password
     * @return a DTO representing the saved entry.
     */
    @PostMapping
    public ResponseEntity<PasswordResponseDTO> createPassword(@RequestBody PasswordRequestDTO request) {
        PasswordResponseDTO response = passwordService.createPassword(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //----------------
    // GET - Read
    //----------------

    /**
     * GET /api/passwords
     * Retrieve all password entries.
     *
     * @return a list of DTOs for each password entry.
     */
    @GetMapping
    public ResponseEntity<List<PasswordResponseDTO>> getAllPasswords() {
        List<PasswordResponseDTO> responses = passwordService.getAllPasswordsDTO();
        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/passwords/{id}
     * Retrieve a single password entry by its ID.
     *
     * @param id the unique identifier.
     * @return a DTO representing the password entry.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PasswordResponseDTO> getPassWordById(@PathVariable Long id) {
        PasswordResponseDTO response = passwordService.getPasswordByIdDTO(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/passwords/search/username?username=example
     * Retrieve password entries with exact username.
     *
     * @param username the username to match for.
     * @return a list of DTOs that matched with a given username.
     */
    @GetMapping("/search/username")
    public ResponseEntity<List<PasswordResponseDTO>> getPasswordsByUsername(@RequestParam String username) {
        List<PasswordResponseDTO> responses = passwordService.getPasswordsByUsernameDTO(username);
        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/passwords/search/service?service=example
     * Retrieve password entries with exact service.
     *
     * @param service the username to search for.
     * @return a list of DTOs that matched with the given service.
     */
    @GetMapping("/search/service")
    public ResponseEntity<List<PasswordResponseDTO>> getPasswordsByService(@RequestParam String service) {
        List<PasswordResponseDTO> responses = passwordService.getPasswordsByServiceDTO(service);
        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/passwords/search/username-like?usernamePattern=%example%
     * Retrieves password entries where the username contains the provided pattern.
     *
     * The caller must include SQL wildcards (e.g. "%pattern%")
     *
     * @param usernamePattern the pattern to search for.
     * @return a list of DTOs for matching password entries.
     */
    @GetMapping("/search/username-like")
    public ResponseEntity<List<PasswordResponseDTO>> getPasswordsByUsernameLike(@RequestParam String usernamePattern) {
        List<PasswordResponseDTO> responses = passwordService.getPasswordsByUsernameLikeDTO(usernamePattern);
        return ResponseEntity.ok(responses);
    }

    /**
     * GET /api/passwords/search/service-like?servicePattern=%example%
     * Retrieves password entries where the service matches the provided pattern.
     *
     * The caller must include wildcards (e.g., "%pattern%").
     *
     * @param servicePattern the pattern to search for.
     * @return a list of DTOs for matching password entries.
     */
    @GetMapping("/search/service-like")
    public ResponseEntity<List<PasswordResponseDTO>> getPasswordsByServiceLike(@RequestParam String servicePattern) {
        List<PasswordResponseDTO> responses = passwordService.getPasswordsByServiceLikeDTO(servicePattern);
        return ResponseEntity.ok(responses);
    }


    //----------------
    // PUT - Update
    //----------------

    /**
     * PUT /api/passwords/{id}
     * Update an existing password entry.
     *
     * @param id the ID of the entry to update.
     * @param request the DTO with updated values.
     * @return a DTO representing the updated entry.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PasswordResponseDTO> updatePassword(@PathVariable Long id, @RequestBody PasswordRequestDTO request) {
        PasswordResponseDTO updatedPassword = passwordService.updatePasswordDTO(id, request);
        return ResponseEntity.ok(updatedPassword);
    }

    //----------------
    // DELETE - Delete
    //----------------

    /**
     * DELETE /api/passwords/{id}
     * Delete a password entry by its ID.
     *
     * @param id the unique identifier.
     * @return an empty response with appropriate status.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassword(@PathVariable Long id) {
        passwordService.deletePassword(id);
        return ResponseEntity.noContent().build();
    }
}
