package packets;

import utils.Constants;
/**
 * Tests the KeepAliveAckPacket to make sure data is package correctly
 * @author Michael Anthony
 */

public class KeepAliveAckPacket extends Packet {
    @Override
    byte[] serialize() {
        return new byte[0];
    }

    @Override
    public byte getOperationCode() {
        return Constants.OPCODE.KEEPALIVEACK;
    }
}
