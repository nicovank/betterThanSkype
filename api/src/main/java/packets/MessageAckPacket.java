package packets;

import utils.Constants;

import java.nio.ByteBuffer;

/**
 * Acknowledgement that a message was received, contains the timestamp of the original message
 *
 * @author Nick Esposito
 *
 */
public class MessageAckPacket extends Packet {
    private final long timestamp;

    public MessageAckPacket(long timestamp) {
        this.timestamp = timestamp;
    }
    public static MessageAckPacket parse(byte[] data)throws InvalidPacketFormatException{
        ByteBuffer buff = ByteBuffer.wrap(data);
        if(buff.remaining()<8){
            throw new InvalidPacketFormatException("Received invalid MESSAGEACK packet");
        }
        long time = buff.getLong();
        return new MessageAckPacket(time);
    }
    @Override
    byte[] serialize() {
        ByteBuffer buff = ByteBuffer.allocate(8);
        buff.putLong(timestamp);
        return buff.array();
    }

    @Override
    public byte getOperationCode() {
        return Constants.OPCODE.MESSAGEACK;
    }

    @Override
    public boolean equals(Object other){
        if(other instanceof MessageAckPacket) {
            return this.timestamp == ((MessageAckPacket) other).timestamp;
        }else{
            return false;
        }
    }
}
