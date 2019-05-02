package packets;

import crypto.CryptoException;
import crypto.RSA;
import org.junit.Test;

import java.security.PublicKey;

import static org.junit.Assert.*;

public class PublicKeyRequestPacketTest {

    @Test
    public void testMirror() throws InvalidPacketFormatException, CryptoException {
        PublicKey key = new RSA().getPublicKey();
        PublicKeyRequestPacket packet = new PublicKeyRequestPacket(key);
        PublicKeyRequestPacket parsed = PublicKeyRequestPacket.parse(packet.serialize());
        assertEquals(packet, parsed);
        assertEquals(packet.getPublicKey(), parsed.getPublicKey());
    }
}
