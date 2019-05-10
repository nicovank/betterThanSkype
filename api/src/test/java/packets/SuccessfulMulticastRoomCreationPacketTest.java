package packets;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.*;

public final class SuccessfulMulticastRoomCreationPacketTest {
    @Test
    public void testParseWithSmallPacket() throws InvalidPacketFormatException, UnknownHostException {
        byte[] data = new byte[]{
                6, 'o', 's', 'w', 'e', 'g', 'o', 0, 0, 0, 6, 's', 'e', 'c', 'r', 'e', 't', 4, 11, 13, 17, 19, 0, 0, 0, 13
        };

        SuccessfulMulticastRoomCreationPacket packet = SuccessfulMulticastRoomCreationPacket.parse(data);
        assertEquals("oswego", packet.getName());
        assertEquals("secret", packet.getSecret());
        assertEquals(InetAddress.getByAddress(new byte[] { 11, 13, 17, 19 }), packet.getIP());
        assertEquals(13, packet.getPort());
    }

    @Test
    public void testSerializeWithSmallPacket() throws UnknownHostException {
        SuccessfulMulticastRoomCreationPacket packet = new SuccessfulMulticastRoomCreationPacket(
                "oswego",
                "secret",
                InetAddress.getByAddress(new byte[]{ 11, 13, 17, 19 }),
                13
        );

        byte[] data = packet.serialize();
        assertArrayEquals(new byte[]{
                6, 'o', 's', 'w', 'e', 'g', 'o', 0, 0, 0, 6, 's', 'e', 'c', 'r', 'e', 't', 4, 11, 13, 17, 19, 0, 0, 0, 13
        }, data);
    }

    @Test(expected = InvalidPacketFormatException.class)
    public void testFailOnEmptyPacket() throws InvalidPacketFormatException {
        SuccessfulMulticastRoomCreationPacket.parse(new byte[0]);
    }

    @Test(expected = InvalidPacketFormatException.class)
    public void testFailOnZeroLength() throws InvalidPacketFormatException {
        SuccessfulMulticastRoomCreationPacket.parse(new byte[]{0});
    }

    @Test(expected = InvalidPacketFormatException.class)
    public void testFailOnMinusOneLength() throws InvalidPacketFormatException {
        SuccessfulMulticastRoomCreationPacket.parse(new byte[]{-1});
    }

    @Test(expected = InvalidPacketFormatException.class)
    public void testFailOnPositiveLengthButInsufficientPacket() throws InvalidPacketFormatException {
        SuccessfulMulticastRoomCreationPacket.parse(new byte[]{7, 'o', 's', 'w', 'e', 'g', 'o'});
    }
}
