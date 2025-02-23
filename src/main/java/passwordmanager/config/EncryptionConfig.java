package passwordmanager.config;

import passwordmanager.util.SecretKeyManager;
import passwordmanager.util.PasswordPromptUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

/**
 * Configuration class for encryption-related beans.
 * Loads the AES secret key from an external file, generating it if necessary.
 */
@Configuration
public class EncryptionConfig {

    // File path where the encrypted key is stored.
    @Value("${encryption.key.filepath}")
    private String encryptionKeyFilePath;


    /**
     * Promts the user for the master, password, loads (or generates) the AES secret key
     * using the master password and key file and exposes it as a Spring bean.
     *
     * If the wrong master password is entered, decryption will fail and the program exits.
     *
     * @return the AES SecretKey
     */
    @Bean
    public SecretKey secretKey() {
        // Prompt the user for the master password via command line.
        String masterPassword = PasswordPromptUtil.promptForMasterPassword();
        try {
            return SecretKeyManager.getSecretKey(masterPassword, encryptionKeyFilePath);
        } catch (Exception e) {
            System.err.println("Failed to load secret key: " + e.getMessage());
            System.err.println("The master password may be incorrect. Exiting program.");
            System.exit(1);
            return null; // Unreachable, but required by compiler.
        }
    }
}
