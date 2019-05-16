package packets;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests the leave room packet
 * @author Mike Doran
 */

public class LeaveRoomPacketTest {
    @Test
    public void testParse() throws InvalidPacketFormatException {
        byte[] data = new byte[]{
                (byte)8, 'n', 'i', 'c', 'k', 'n', 'a', 'm', 'e',
                (byte)8, 'r', 'o', 'o', 'm', 'n', 'a', 'm', 'e'
        };
        LeaveRoomPacket packet = LeaveRoomPacket.parse(data);

        assertEquals("nickname", packet.getNickname());
        assertEquals("roomname", packet.getRoomname());
    }

    @Test
    public void testSerialize(){
        LeaveRoomPacket packet = new LeaveRoomPacket("nickname", "roomname");
        byte[] data = new byte[]{
                (byte)8, 'n', 'i', 'c', 'k', 'n', 'a', 'm', 'e',
                (byte)8, 'r', 'o', 'o', 'm', 'n', 'a', 'm', 'e'
        };
        assertArrayEquals(data, packet.serialize());
    }

}
