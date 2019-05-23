package packets;

import utils.Constants;
/**
 * Acknowledgement packet for announcement acks
 *
 * @author Nick Esposito
 */
public class AnnounceAckAckPacket extends Packet {

    public static AnnounceAckAckPacket parse(byte[] data) throws InvalidPacketFormatException {
        if (data.length != 0) {
            System.out.println(data.length);
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
        return Constants.OPCODE.ANNACKACK;
    }

    @Override
    public boolean equals(Object other){
        return other instanceof AnnounceAckAckPacket;
    }
}
