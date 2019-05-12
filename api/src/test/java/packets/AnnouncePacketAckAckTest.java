package packets;

import org.junit.Test;

import static org.junit.Assert.*;

public class AnnouncePacketAckAckTest {
    @Test
    public void testSerialize(){
        AnnounceAckAckPacket packet = new AnnounceAckAckPacket();
        byte[] data = new byte[0];
        assertArrayEquals(data, packet.serialize());
    }
}
