package packets;

import org.junit.Test;

import static org.junit.Assert.*;

public class SuccessfulRoomCreationPacketTest {
    @Test
    public void testParseWithSmallPacket() throws InvalidPacketFormatException {
        byte[] data = new byte[]{
                6, 'o', 's', 'w', 'e', 'g', 'o'
        };

        SuccessfulRoomCreationPacket packet = SuccessfulRoomCreationPacket.parse(data);
        assertEquals("oswego", packet.getName());
    }

    @Test
    public void testSerializeWithSmallPacket() {
        SuccessfulRoomCreationPacket packet = new SuccessfulRoomCreationPacket("oswego");
        byte[] data = packet.serialize();
        assertArrayEquals(new byte[]{
                6, 'o', 's', 'w', 'e', 'g', 'o'
        }, data);
    }

    @Test(expected = InvalidPacketFormatException.class)
    public void testFailOnEmptyPacket() throws InvalidPacketFormatException {
        SuccessfulRoomCreationPacket.parse(new byte[0]);
    }

    @Test(expected = InvalidPacketFormatException.class)
    public void testFailOnZeroLength() throws InvalidPacketFormatException {
        SuccessfulRoomCreationPacket.parse(new byte[]{0});
    }

    @Test(expected = InvalidPacketFormatException.class)
    public void testFailOnMinusOneLength() throws InvalidPacketFormatException {
        SuccessfulRoomCreationPacket.parse(new byte[]{-1});
    }

    @Test(expected = InvalidPacketFormatException.class)
    public void testFailOnPositiveLengthButInsufficientPacket() throws InvalidPacketFormatException {
        SuccessfulRoomCreationPacket.parse(new byte[]{7, 'o', 's', 'w', 'e', 'g', 'o'});
    }
}
