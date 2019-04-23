package crypto;

import org.junit.Test;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Random;

public class RSATest {

    private KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(4096);
        return generator.generateKeyPair();
    }

    @Test
    public void decodeKeyTest() throws GeneralSecurityException, CryptoException {
        KeyPair pair = generateRSAKeyPair();
        assertEquals(pair.getPublic(), RSA.decodePublicKey(pair.getPublic().getEncoded()));
    }

    @Test
    public void RSATestOnSmallString() throws GeneralSecurityException, CryptoException {
        KeyPair pair = generateRSAKeyPair();
        RSA rsa = new RSA(pair);

        String message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed efficitur velit ante, id aliquam est.";

        byte[] encrypted = RSA.encrypt(message.getBytes(StandardCharsets.UTF_8), pair.getPublic());
        byte[] decrypted = rsa.decrypt(encrypted);
        assertEquals(message, new String(decrypted, StandardCharsets.UTF_8));
    }

    @Test
    public void RSATestOnArrayOfRandomBytes() throws GeneralSecurityException, CryptoException {
        KeyPair pair = generateRSAKeyPair();
        RSA rsa = new RSA(pair);

        byte[] array = new byte[1313];
        Random random = new Random();
        random.nextBytes(array);

        byte[] encrypted = RSA.encrypt(array, pair.getPublic());
        byte[] decrypted = rsa.decrypt(encrypted);
        assertArrayEquals(array, decrypted);
    }
}
