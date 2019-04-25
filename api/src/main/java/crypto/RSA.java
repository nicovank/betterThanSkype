package crypto;

import javax.crypto.*;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * This class provides utilities for encryption and decryption of messages using RSA.
 * Its methods are based on the builtin Java methods from `java.security` and
 * `javax.crypto`.
 */
public final class RSA {

    private static final int KEYSIZE = 4096;
    private static final int ENCRYPTED_CHUNK_SIZE = KEYSIZE / 8;
    private static final int CHUNK_SIZE = ENCRYPTED_CHUNK_SIZE - 11;

    private final PublicKey pub;
    private final PrivateKey pvt;

    /**
     * This constructor creates a new RSA object, initializing it with new keys.
     *
     * @throws CryptoException If there was any error generating the key pair.
     */
    public RSA() throws CryptoException {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(KEYSIZE);
            KeyPair pair = generator.generateKeyPair();

            this.pub = pair.getPublic();
            this.pvt = pair.getPrivate();
        } catch (GeneralSecurityException e) {
            throw new CryptoException("Could not successfully generate public and private keys.", e);
        }
    }

    public RSA(PublicKey pub, PrivateKey pvt) {
        this.pub = pub;
        this.pvt = pvt;
    }

    public RSA(KeyPair keys) {
        this(keys.getPublic(), keys.getPrivate());
    }

    public byte[] getPublicKey() {
        return pub.getEncoded();
    }

    /**
     * This method decodes a key from its encoded, byte array version.
     *
     * @param encoded the encoded public key.
     * @return The public key given, as a `PublicKey` object.
     * @throws CryptoException if there were any issues decoding the key.
     */
    public static PublicKey decodePublicKey(byte[] encoded) throws CryptoException {
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePublic(new X509EncodedKeySpec(encoded));
        } catch (GeneralSecurityException e) {
            throw new CryptoException("An issue occurred while decoding the key.", e);
        }
    }

    /**
     * This method encrypts the given data using the given public key.
     * The returned byte array's size will always be a multiple of 512.
     *
     * @param data The data to be encrypted.
     * @param key  The key to use for encryption (the intended recipient's public key)
     * @return the encrypted data as a byte array.
     * @throws CryptoException if there were any issues with the provided key or the machine implementation of the crypto algorithms.
     */
    public static byte[] encrypt(byte[] data, PublicKey key) throws CryptoException {
        try {

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            int blocks = (data.length / CHUNK_SIZE) + ((data.length % CHUNK_SIZE == 0) ? 0 : 1);
            byte[] encrypted = new byte[blocks * ENCRYPTED_CHUNK_SIZE];

            for (int i = 0; i < blocks; ++i) {
                int to = Math.min(data.length, CHUNK_SIZE * i + CHUNK_SIZE);
                byte[] chunk = Arrays.copyOfRange(data, CHUNK_SIZE * i, to);
                byte[] encryptedchunk = cipher.doFinal(chunk);

                System.arraycopy(encryptedchunk, 0, encrypted, ENCRYPTED_CHUNK_SIZE * i, ENCRYPTED_CHUNK_SIZE);
            }

            return encrypted;

        } catch (GeneralSecurityException e) {
            // TODO
            // We can be more precise about error reporting by catching the subclasses of `GeneralSecurityException` instead.
            throw new CryptoException("An issue occurred while encrypting the data.", e);
        }
    }

    /**
     * This method decrypts the given encrypted data using this RSA object's private key.
     * It is therefore assumed the data was encrypted using this object's public key.
     *
     * @param encrypted the byte array to decrypt.
     * @return teh original data that was encrypted.
     * @throws CryptoException if there were any issue decrypting the data.
     */
    public byte[] decrypt(byte[] encrypted) throws CryptoException {
        try {

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, pvt);

            int blocks = encrypted.length / ENCRYPTED_CHUNK_SIZE;
            ByteArrayOutputStream out = new ByteArrayOutputStream(blocks * CHUNK_SIZE);

            for (int i = 0; i < blocks; ++i) {
                byte[] encryptedchunk = Arrays.copyOfRange(encrypted, ENCRYPTED_CHUNK_SIZE * i, ENCRYPTED_CHUNK_SIZE * i + ENCRYPTED_CHUNK_SIZE);
                byte[] chunk = cipher.doFinal(encryptedchunk);

                out.writeBytes(chunk);
            }

            return out.toByteArray();

        } catch (GeneralSecurityException e) {
            // TODO
            // We can be more precise about error reporting by catching the subclasses of `GeneralSecurityException` instead.
            throw new CryptoException("An issue occurred while decrypting the data.", e);
        }
    }
}
