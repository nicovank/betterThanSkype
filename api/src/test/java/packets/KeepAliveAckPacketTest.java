package packets;

import org.junit.Test;
import utils.Constants;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class KeepAliveAckPacketTest {

    @Test
    public void testSerializeWithSmallPacket(){
        KeepAliveAckPacket packet = new KeepAliveAckPacket();
        byte[] testArray = new byte[0];

        assertArrayEquals(testArray, packet.serialize());
        assertEquals(Constants.OPCODE.KEEPALIVEACK, packet.getOperationCode());
    }


}
