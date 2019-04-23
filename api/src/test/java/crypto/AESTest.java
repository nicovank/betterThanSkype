package crypto;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class AESTest {

    private byte[] generateAESSecretKey() throws NoSuchAlgorithmException {
        Random random = new Random();
        byte[] array = new byte[random.nextInt(4095) + 1];
        random.nextBytes(array);
        return array;
    }

    @Test
    public void AESTestOnSmallString() throws GeneralSecurityException, CryptoException {
        byte[] key = generateAESSecretKey();
        AES aes = new AES(key);

        String message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed efficitur velit ante, id aliquam est.";

        byte[] encrypted = aes.encrypt(message.getBytes(StandardCharsets.UTF_8));
        byte[] decrypted = aes.decrypt(encrypted);
        assertEquals(message, new String(decrypted, StandardCharsets.UTF_8));
    }

    @Test
    public void AESTestOnArrayOfRandomBytes() throws GeneralSecurityException, CryptoException {
        byte[] key = generateAESSecretKey();
        AES aes = new AES(key);

        byte[] array = new byte[1313];
        Random random = new Random();
        random.nextBytes(array);

        byte[] encrypted = aes.encrypt(array);
        byte[] decrypted = aes.decrypt(encrypted);
        assertArrayEquals(array, decrypted);
    }
}
