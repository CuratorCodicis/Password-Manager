package passwordmanager.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Serivce for AES encryption and decryption.
 * Uses the injected SecretKey (provided by EncryptionConfig)
 */
@Service
public class AESEncryptionService implements EncryptionService{

    // Using AES algorithm in CBC mode with PKCS5 padding.
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    // Using a 16-byte Initialization Vector (IV).
    private static final int IV_SIZE = 16;
    // Injected secret key from Spring context.
    private final SecretKey secretKey;

    /**
     * Constructor injection: Spring will automatically provide the SecretKey bean
     * defined in EncryptionConfig.
     *
     * @param secretKey the AES secret Key loaded from an external file.
     */
    public AESEncryptionService(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public byte[] encrypt(String plaintext) {
        try {
            // Generate random IV
            byte[] iv = new byte[IV_SIZE];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);
            IvParameterSpec  ivSpec = new IvParameterSpec(iv);

            // Initialize cipher in encryption mode
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            // Encrypt the plaintext
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Prepend the IV to the encrypted data (so it can be used in decryption)
            byte[] encryptedWithIv = new byte[IV_SIZE + encrypted.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, IV_SIZE);
            System.arraycopy(encrypted, 0, encryptedWithIv, IV_SIZE, encrypted.length);
            return encryptedWithIv;
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting", e);
        }
    }

    @Override
    public String decrypt(byte[] ciphertext) {
        try {
            // Extract the IV from the first IV_SIZE bytes of the ciphertext.
            byte[] iv = Arrays.copyOfRange(ciphertext, 0, IV_SIZE);
            byte[] actualCiphertext = Arrays.copyOfRange(ciphertext, IV_SIZE, ciphertext.length);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Initialize cipher in decryption mode
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            // Decrypt the ciphertext
            byte[] decrypted = cipher.doFinal(actualCiphertext);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting", e);
        }

    }
}
