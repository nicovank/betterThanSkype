package packets;

import org.junit.Test;
import utils.Constants;

import static org.junit.Assert.*;

public class RoomCreationRequestPacketTest {
    @Test
    public void testSerializeWithEmptyPassword() {
        RoomCreationRequestPacket packet = new RoomCreationRequestPacket("oswego", "room", "", Constants.TYPE.UNICAST);
        assertArrayEquals(new byte[]{
            6, 'o', 's', 'w', 'e', 'g', 'o', 4, 'r', 'o', 'o', 'm', 0, 0, 0, 0, Constants.TYPE.UNICAST
        }, packet.serialize());
    }

    @Test
    public void testSerializeWithSmallPassword() {
        RoomCreationRequestPacket packet = new RoomCreationRequestPacket("oswego", "room", "password", Constants.TYPE.MULTICAST);
        assertArrayEquals(new byte[]{
                6, 'o', 's', 'w', 'e', 'g', 'o', 4, 'r', 'o', 'o', 'm', 0, 0, 0, 8, 'p', 'a', 's', 's', 'w', 'o', 'r', 'd', Constants.TYPE.MULTICAST
        }, packet.serialize());
    }

    @Test
    public void testParseWithEmptyPassword() throws InvalidPacketFormatException {
        byte[] data = new byte[]{
                6, 'o', 's', 'w', 'e', 'g', 'o', 4, 'r', 'o', 'o', 'm', 0, 0, 0, 0, Constants.TYPE.MULTICAST
        };

        RoomCreationRequestPacket packet = RoomCreationRequestPacket.parse(data);
        assertEquals("oswego", packet.getUserName());
        assertEquals("room", packet.getRoomName());
        assertEquals("", packet.getPassword());
        assertEquals(Constants.TYPE.MULTICAST, packet.getType());
    }



    @Test
    public void testParseWithSmallPassword() throws InvalidPacketFormatException {
        byte[] data = new byte[]{
                6, 'o', 's', 'w', 'e', 'g', 'o', 4, 'r', 'o', 'o', 'm', 0, 0, 0, 8, 'p', 'a', 's', 's', 'w', 'o', 'r', 'd', Constants.TYPE.UNICAST
        };

        RoomCreationRequestPacket packet = RoomCreationRequestPacket.parse(data);
        assertEquals("oswego", packet.getUserName());
        assertEquals("room", packet.getRoomName());
        assertEquals("password", packet.getPassword());
        assertEquals(Constants.TYPE.UNICAST, packet.getType());
    }
}
