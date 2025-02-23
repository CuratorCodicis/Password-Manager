package passwordmanager.exception;

/**
 * Exception thrown when a request resource (e.g. password entry) is not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
