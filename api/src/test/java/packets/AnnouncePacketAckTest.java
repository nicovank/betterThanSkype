package packets;

import org.junit.Test;

import static org.junit.Assert.*;

public class AnnouncePacketAckTest {

    @Test
    public void testParse() throws InvalidPacketFormatException {
        byte[] data = new byte[]{
                8, 'n', 'i', 'c', 'k', 'n', 'a', 'm', 'e',
                5, 'o', 't', 'h', 'e', 'r',
                0,0,0,8, 'p', 'a', 's', 's', 'w', 'o', 'r', 'd',
                0, 0, 0, 0, 0, 0, 0, 1
        };
        AnnounceAckPacket packet = AnnounceAckPacket.parse(data);

        assertEquals("nickname", packet.getNickName());
        assertEquals("password", packet.getPassword());
        assertEquals((long)1, packet.getTimestamp());
    }

    @Test
    public void testSerialize(){
        AnnounceAckPacket packet = new AnnounceAckPacket("nickname", "other", "password", 1);
        byte[] data = new byte[]{
                8, 'n', 'i', 'c', 'k', 'n', 'a', 'm', 'e',
                5, 'o', 't', 'h', 'e', 'r',
                0,0,0,8, 'p', 'a', 's', 's', 'w', 'o', 'r', 'd',
                0, 0, 0, 0, 0, 0, 0, 1
        };

        assertArrayEquals(data, packet.serialize());
    }
}
