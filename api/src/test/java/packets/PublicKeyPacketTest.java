package packets;

import crypto.CryptoException;
import crypto.RSA;
import org.junit.Test;

import java.security.PublicKey;

import static org.junit.Assert.*;

public class PublicKeyPacketTest {

    @Test
    public void testMirror() throws InvalidPacketFormatException, CryptoException {
        PublicKey key = new RSA().getPublicKey();
        PublicKeyPacket packet = new PublicKeyPacket(key);
        PublicKeyPacket parsed = PublicKeyPacket.parse(packet.serialize());
        assertEquals(packet, parsed);
        assertEquals(packet.getPublicKey(), parsed.getPublicKey());
    }
}
