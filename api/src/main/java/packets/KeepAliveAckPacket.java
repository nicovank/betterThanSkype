package packets;

import utils.Constants;
/**
 * Tests the KeepAliveAckPacket to make sure data is package correctly
 * @author Michael Anthony
 */

public class KeepAliveAckPacket extends Packet {

    public static AnnounceAckAckPacket parse(byte[] data) throws InvalidPacketFormatException {
        if (data.length != 0) {
            throw new InvalidPacketFormatException("Invalid AAA packet received.");
        }

        return new AnnounceAckAckPacket();
    }

    @Override
    byte[] serialize() {
        return new byte[0];
    }

    @Override
    public byte getOperationCode() {
        return Constants.OPCODE.KEEPALIVEACK;
    }
}
