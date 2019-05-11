package packets;

import org.junit.Test;
import utils.Constants;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.*;

public class JoinRoomSuccessPacketTest {

    @Test
    public void testSerializeWithSmallPacket() throws UnknownHostException {
        JoinRoomSuccessPacket packet = new JoinRoomSuccessPacket(
                "oswego",
                "secret",
                InetAddress.getByAddress(new byte[]{ 11, 13, 17, 19 }),
                13,
                Constants.TYPE.MULTICAST
        );

        assertArrayEquals(new byte[]{
                6, 'o', 's', 'w', 'e', 'g', 'o', 0, 0, 0, 6, 's', 'e', 'c', 'r', 'e', 't', Constants.TYPE.MULTICAST, 4, 11, 13, 17, 19, 0, 0, 0, 13
        }, packet.serialize());
    }

    @Test
    public void testParseWithSmallPacket() {
        byte[] data = new byte[]{
                6, 'o', 's', 'w', 'e', 'g', 'o', 0, 0, 0, 6, 's', 'e', 'c', 'r', 'e', 't', Constants.TYPE.MULTICAST, 4, 11, 13, 17, 19, 0, 0, 0, 13
        };


    }
}
