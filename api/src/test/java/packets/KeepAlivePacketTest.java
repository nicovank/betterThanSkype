package packets;


import org.junit.Test;
import utils.Constants;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Tests the KeepAlivePacket to make sure data is package correctly
 * @author Michael Anthony
 */


public class KeepAlivePacketTest {



    @Test
    public void testSerializeWithSmallPacket(){
        KeepAlivePacket packet = new KeepAlivePacket();
        byte[] testArray = new byte[0];

        assertArrayEquals(testArray, packet.serialize());
        assertEquals(Constants.OPCODE.KEEPALIVE, packet.getOperationCode());
    }
}
