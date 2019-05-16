package packets;

import utils.Constants;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author Nick esposito
 *
 * Packet that contains the actual messages sent between clients
 */
public class MessagePacket extends Packet {
    public String getNickName() {
        return nickName;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    private final String nickName;
    private final String message;
    private final long timestamp;

    public MessagePacket(String nickName, String message, long timestamp) {
        this.nickName = nickName;
        this.message = message;
        this.timestamp = timestamp;
    }
    public static MessagePacket parse(byte[] data) throws InvalidPacketFormatException{
        ByteBuffer buff = ByteBuffer.wrap(data);
        if (buff.remaining()<15){
            throw new InvalidPacketFormatException("Received invalid MESSAGE packet.");
        }

        byte nlength = buff.get();
        if (nlength <= 0 || nlength > 32) {
            throw new InvalidPacketFormatException("Received invalid MESSAGE packet.");
        }

        byte[] name = new byte[nlength];
        buff.get(name);

        int mlength = buff.getInt();
        if(mlength <=0 || mlength > 900){
            throw new InvalidPacketFormatException("Received invalid MESSAGE packet.");

        }
        byte[] message = new byte[mlength];
        buff.get(message);

        long timestamp = buff.getLong();
        return new MessagePacket(new String(name, StandardCharsets.UTF_8), new String(message,StandardCharsets.UTF_8),timestamp);
    }
    public MessageAckPacket createAck(){
        return new MessageAckPacket(timestamp);
    }
    @Override
    byte[] serialize() {
        byte[] name = this.nickName.getBytes(StandardCharsets.UTF_8);
        byte[] message = this.message.getBytes(StandardCharsets.UTF_8);

        ByteBuffer buff = ByteBuffer.allocate(name.length + message.length + 13);

        buff.put((byte) name.length);
        buff.put(name);
        buff.putInt(message.length);
        buff.put(message);
        buff.putLong(timestamp);
        return buff.array();
    }

    @Override
    public byte getOperationCode() {
        return Constants.OPCODE.MESSAGE;
    }
    @Override
    public int hashCode() {
        return Objects.hash(nickName, message,timestamp);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MessagePacket)) return false;
        MessagePacket o = (MessagePacket) other;
        return o.nickName.equals(this.nickName) && o.message.equals(this.message) && o.timestamp==this.timestamp;
    }
}
/*
          1           <= 32       4              <=900        8

     | nickLength | nickname |  messageLength | message | timestamp |

 */