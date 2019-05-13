package packets;

import org.junit.Test;

import static org.junit.Assert.*;

public class AnnouncePacketTest {

    @Test
    public void testParse() throws InvalidPacketFormatException {
        byte[] data = new byte[]{
                (byte)8, 'n', 'i', 'c', 'k', 'n', 'a', 'm', 'e',
                (byte)8, 'p', 'a', 's', 's', 'w', 'o', 'r', 'd'
        };
        AnnouncePacket packet = AnnouncePacket.parse(data);

        assertEquals("nickname", packet.getNickName());
        assertEquals("password", packet.getPassword());
    }

    @Test
    public void testSerialize(){
        AnnouncePacket packet = new AnnouncePacket("nickname", "password");
        byte[] data = new byte[]{
                (byte)8, 'n', 'i', 'c', 'k', 'n', 'a', 'm', 'e',
                (byte)8, 'p', 'a', 's', 's', 'w', 'o', 'r', 'd'
        };

        assertArrayEquals(data, packet.serialize());
    }
}
