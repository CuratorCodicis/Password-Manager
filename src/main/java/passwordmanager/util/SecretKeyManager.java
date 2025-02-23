package passwordmanager.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

/**
 * Utility class for managing the AES secret ket.
 *
 * This class implements key management as follows:
 * 1. When the program starts, it checks if a key file exists.
 * 2. If not, it generates a new 256-bit AES key, encrypts it using a key derived
 *    from the user's mater password (via PBKDF2WithHmacSHA256), and writes the
 *    following binary data to the key file:
 *      [16 bytes salt] [16 bytes IV] [encrypted AES key bytes]
 * 3. If the key file exists, it reads these components, derives the key from the
 *    provided master password using the stored salt, and decrypts the AES key.
 *
 * The master password is supplied to this method and must be correct in order
 * for the decryption to succeed.
 */
public class SecretKeyManager {

    // Constants: sizes in bytes
    private static final int SALT_SIZE = 16;
    private static final int IV_SIZE = 16;
    private static final int AES_KEY_SIZE = 32; // 256 bits

    // Password-Based Key Derivation Function 2 (PBKDF2) configuration
    private static final int ITERATION_COUNT = 600000;
    private static final int DERIVED_KEY_LENGTH = 256; // bits

    /**
     * Loads the AES secret key from the specified file. If the file does not exist,
     * generates a new AES key, encrypts it using the master password, and writes it to the file.
     */
    public static SecretKey getSecretKey(String masterPassword, String keyFilePath) {
        File keyFile = new File(keyFilePath);
        if (keyFile.exists()) {
            // Key file exists; load and decrypt the key
            System.out.println("Key file found at: " + keyFile.getAbsolutePath());
            try {
                byte[] fileData = Files.readAllBytes(keyFile.toPath());
                if (fileData.length < SALT_SIZE + IV_SIZE) {
                    throw new RuntimeException("Key file is too short or corrupted.");
                }
                // Extract salt, IV, and encrypted key from the file.
                byte[] salt = Arrays.copyOfRange(fileData, 0, SALT_SIZE);
                byte[] iv = Arrays.copyOfRange(fileData, SALT_SIZE, SALT_SIZE + IV_SIZE);
                byte[] encryptedKey = Arrays.copyOfRange(fileData, SALT_SIZE + IV_SIZE, fileData.length);

                // Derive the decryption key from the master password and salt.
                SecretKey derivedKey = deriveKeyFromPassword(masterPassword, salt);

                // Decrypt the stored AES key.
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, derivedKey, new IvParameterSpec(iv));
                byte[] aesKeyBytes = cipher.doFinal(encryptedKey);
                return new SecretKeySpec(aesKeyBytes, "AES");
            } catch (Exception e) {
                throw new RuntimeException("Failed to load secret key: " + e.getMessage(), e);
            }
        } else {
            // Key file does not exist; generate a new AES key and store it.
            System.out.println("No key file found at: " + keyFile.getAbsolutePath());
            System.out.println("You will not be able to decrypt existing database entries.");
            System.out.println();
            try {
                SecureRandom random = new SecureRandom();

                // Generate new AES key (256-bit random key)
                byte[] aesKeyBytes = new byte[AES_KEY_SIZE];
                random.nextBytes(aesKeyBytes);
                SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

                // Generate a random salt.
                byte[] salt = new byte[SALT_SIZE];
                random.nextBytes(salt);

                // Derive an encryption key from the master password and salt.
                SecretKey derivedKey = deriveKeyFromPassword(masterPassword, salt);

                // Generate a random IV for encryption.
                byte[] iv = new byte[IV_SIZE];
                random.nextBytes(iv);
                IvParameterSpec ivSpec = new IvParameterSpec(iv);

                // Encrypt the newly generated AES key.
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, derivedKey, ivSpec);
                byte[] encryptedKey = cipher.doFinal(aesKeyBytes);

                // Concatenate salt + IV + encrypted Key into a single byte array.
                byte[] fileData = new byte[SALT_SIZE + IV_SIZE + encryptedKey.length];
                System.arraycopy(salt, 0, fileData, 0, SALT_SIZE);
                System.arraycopy(iv, 0, fileData, SALT_SIZE, IV_SIZE);
                System.arraycopy(encryptedKey, 0, fileData, SALT_SIZE + IV_SIZE, encryptedKey.length);

                // Write the combined data to the key file
                Files.write(Path.of(keyFilePath), fileData);
                System.out.println("-> Generating new secret ... \nNew key file created at: " + keyFile.getAbsolutePath());
                return aesKey;


            } catch (Exception e) {
                throw new RuntimeException("Failed to generate and store secret key: " + e.getMessage(), e);
            }

        }
    }

    /**
     * Derives a SecretKey fomr a master password and salt using PBKDF2 with HMAC SHA256.
     *
     * @param masterPassword the master password
     * @param salt the salt (16 bytes)
     * @return the derived SecretKey
     * @throws Exception if key derivation fails
     */
    private static SecretKey deriveKeyFromPassword(String masterPassword, byte[] salt) throws Exception {
        KeySpec spec = new PBEKeySpec(masterPassword.toCharArray(), salt, ITERATION_COUNT, DERIVED_KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }
}
