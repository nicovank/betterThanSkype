package packets;

import org.junit.Test;
import utils.Constants;

import static org.junit.Assert.*;

public class JoinRoomRequestPacketTest {

    @Test
    public void testSerializeWithSmallPacket() {
        JoinRoomRequestPacket packet = new JoinRoomRequestPacket("oswego", "room", "password", Constants.TYPE.MULTICAST);

        assertArrayEquals(new byte[] {
            6, 'o', 's', 'w', 'e', 'g', 'o', 4, 'r', 'o', 'o', 'm', 0, 0, 0, 8, 'p', 'a', 's', 's', 'w', 'o', 'r', 'd', Constants.TYPE.MULTICAST
        }, packet.serialize());
    }

    @Test
    public void testParseWithSmallPacket() throws InvalidPacketFormatException {
        byte[] data = new byte[] {
                6, 'o', 's', 'w', 'e', 'g', 'o', 4, 'r', 'o', 'o', 'm', 0, 0, 0, 8, 'p', 'a', 's', 's', 'w', 'o', 'r', 'd', Constants.TYPE.UNICAST
        };

        JoinRoomRequestPacket packet = JoinRoomRequestPacket.parse(data);
        assertEquals("oswego", packet.getUserName());
        assertEquals("room", packet.getRoomName());
        assertEquals("password", packet.getPassword());
        assertEquals(Constants.TYPE.UNICAST, packet.getType());
    }
}
