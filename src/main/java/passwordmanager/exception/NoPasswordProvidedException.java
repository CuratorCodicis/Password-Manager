package passwordmanager.exception;

/**
 * Exception thrown when no plaintext password is provided in a create request
 */
public class NoPasswordProvidedException extends RuntimeException{
    public NoPasswordProvidedException(String message) {
        super(message);
    }
}
