package crypto;

import org.junit.Test;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Random;

public class CryptoTest {

    private KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(4096);
        return generator.generateKeyPair();
    }

    @Test
    public void decodeKeyTest() throws GeneralSecurityException, CryptoException {
        KeyPair pair = generateKeyPair();
        assertEquals(pair.getPublic(), Crypto.decodeKey(pair.getPublic().getEncoded()));
    }

    @Test
    public void cryptoTestOnSmallString() throws GeneralSecurityException, CryptoException {
        KeyPair pair = generateKeyPair();
        Crypto crypto = new Crypto(pair);

        String message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed efficitur velit ante, id aliquam est.";

        byte[] encrypted = Crypto.encrypt(message.getBytes(StandardCharsets.UTF_8), pair.getPublic());
        byte[] decrypted = crypto.decrypt(encrypted);
        assertEquals(message, new String(decrypted, StandardCharsets.UTF_8));
    }

    @Test
    public void cryptoTestOnArrayOfRandomBytes() throws GeneralSecurityException, CryptoException {
        KeyPair pair = generateKeyPair();
        Crypto crypto = new Crypto(pair);

        byte[] array = new byte[1313];
        Random random = new Random();
        random.nextBytes(array);

        byte[] encrypted = Crypto.encrypt(array, pair.getPublic());
        byte[] decrypted = crypto.decrypt(encrypted);
        assertArrayEquals(array, decrypted);
    }
}
