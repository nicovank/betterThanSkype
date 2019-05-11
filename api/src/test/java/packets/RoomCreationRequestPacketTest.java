package packets;

import org.junit.Test;
import utils.Constants;

import static org.junit.Assert.*;

public class RoomCreationRequestPacketTest {
//    @Test
//    public void testSerializeWithEmptyPassword() {
//        RoomCreationRequestPacket packet = new RoomCreationRequestPacket("oswego", "", Constants.TYPE.UNICAST);
//        assertArrayEquals(new byte[]{
//            6, 'o', 's', 'w', 'e', 'g', 'o', 0, 0, 0, 0, Constants.TYPE.UNICAST
//        }, packet.serialize());
//    }
//
//    @Test
//    public void testSerializeWithSmallPassword() {
//        RoomCreationRequestPacket packet = new RoomCreationRequestPacket("oswego", "oswego", Constants.TYPE.MULTICAST);
//        assertArrayEquals(new byte[]{
//                6, 'o', 's', 'w', 'e', 'g', 'o', 0, 0, 0, 6, 'o', 's', 'w', 'e', 'g', 'o', Constants.TYPE.MULTICAST
//        }, packet.serialize());
//    }
//
//    @Test
//    public void testParseWithEmptyPassword() throws InvalidPacketFormatException {
//        byte[] data = new byte[]{
//                6, 'o', 's', 'w', 'e', 'g', 'o', 0, 0, 0, 0, Constants.TYPE.MULTICAST
//        };
//
//        RoomCreationRequestPacket packet = RoomCreationRequestPacket.parse(data);
//        assertEquals("oswego", packet.getName());
//        assertEquals("", packet.getSecret());
//        assertEquals(Constants.TYPE.MULTICAST, packet.getType());
//    }
//
//
//
//    @Test
//    public void testParseWithSmallPassword() throws InvalidPacketFormatException {
//        byte[] data = new byte[]{
//                6, 'o', 's', 'w', 'e', 'g', 'o', 0, 0, 0, 6, 'o', 's', 'w', 'e', 'g', 'o', Constants.TYPE.UNICAST
//        };
//
//        RoomCreationRequestPacket packet = RoomCreationRequestPacket.parse(data);
//        assertEquals("oswego", packet.getName());
//        assertEquals("oswego", packet.getSecret());
//        assertEquals(Constants.TYPE.UNICAST, packet.getType());
//    }
}
