package packets;

import utils.Constants;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


/**
 * Tests the MessageAckPacket to make sure data is package correctly
 * @author Michael Anthony
 */


public class MessageAckPacketTest {

    public void testSerializeWithSmallPacket() {
        long input = 123456;
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(input);
        byte[] bArray = buffer.array();
        MessageAckPacket packet = new MessageAckPacket(input);
        assertArrayEquals(bArray, packet.serialize());

    }

    public void testParseithSmallPacket() throws  InvalidPacketFormatException {
        long input = 123456;
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(input);
        byte[] bArray = buffer.array();
        MessageAckPacket packet =  MessageAckPacket.parse(bArray);
        assertEquals(packet,MessagePacket.parse(bArray));
        assertEquals(Constants.OPCODE.MESSAGEACK, packet.getOperationCode());

    }


}
