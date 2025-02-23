package passwordmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler that catches exceptions thrown by controllers
 * and return consistent error responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundExceptions and returns a 404 response.
     *
     * @param ex the exception thrown.
     * @return ResponseEntity with error message and HTTP status 404.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Handles DuplicateResourceExceptions and returns a 409 response.
     *
     * @param ex the exception thrown.
     * @return ResponseEntity with error message and HTTP status 409.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<String> handleDuplicateResource(DuplicateResourceException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    /**
     * Handles NoPasswordProvidedExceptions and returns a HTTP status 400.
     *
     * @param ex the exception thrown.
     * @return ResponseEntity with error message and HTTP status 400.
     */
    @ExceptionHandler(NoPasswordProvidedException.class)
    public ResponseEntity<String> handleNoPasswordProvidedException(NoPasswordProvidedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Generic handler for any other exceptions.
     *
     * @param ex the exception thrown.
     * @return ResponseEntity with error message and HTTP status 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return new ResponseEntity<>("An unexpected error occured: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
