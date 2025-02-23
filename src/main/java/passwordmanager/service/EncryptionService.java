package passwordmanager.service;

public interface EncryptionService {
    /**
     * Encrypts the given plaintext.
     *
     * @param plaintext the text to encrypt.
     * @return the encrypted data as a byte array.
     */
    byte[] encrypt(String plaintext);

    /**
     * Decrypts the given byte array into a plaintext string.
     *
     * @param ciphertext the encrypted data.
     * @return the decrypted plaintext.
     */
    String decrypt(byte[] ciphertext);
}
