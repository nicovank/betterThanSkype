package crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class provides utilities for encryption and decryption of messages using AES.
 * Its methods are based on the builtin Java methods from `java.security` and
 * `javax.crypto`.
 */
public final class AES {

    private static SecretKey key;

    /**
     * This constructor creates a new AES object with the given secret as key.
     *
     * @param secret The secret to use as a key.
     * @throws CryptoException If there was any errors generating the secret key.
     */
    public AES(byte[] secret) throws CryptoException {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = new SecretKeySpec(sha.digest(secret), "AES");
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("An issue occurred while generating the secret key from the secret.", e);
        }
    }

    public AES(String secret) throws CryptoException {
        this(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * This method encrypts the given data using the secret key stored.
     *
     * @param data the data to encrypt.
     * @return the encrypted data.
     * @throws CryptoException if there were any issues encrypting the data.
     */
    public byte[] encrypt(byte[] data) throws CryptoException {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new CryptoException("An issue occurred while encrypting the data.", e);
        }
    }

    /**
     * This method decrypts the given data using the secret stored.
     *
     * @param encrypted the bytes to read the encrypted original message from.
     * @return the original message, assuming it was encrypted using the same secret.
     * @throws CryptoException if there were any issues decrypting the data.
     */
    public byte[] decrypt(byte[] encrypted) throws CryptoException {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(encrypted);
        } catch (GeneralSecurityException e) {
            throw new CryptoException("An issue occurred while decrypting the data.", e);
        }
    }
}
