package packets;

import utils.Constants;

public class KeepAlivePacket extends Packet {
    @Override
    byte[] serialize() {
        return new byte[0];
    }

    @Override
    public byte getOperationCode() {
        return Constants.OPCODE.KEEPALIVE;
    }
}
