package packets;

import utils.Constants;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class AnnounceAckPacket extends Packet {
    private final String nickName;
    private final String password;
    private final long timestamp;

    public String getNickName() {
        return nickName;
    }

    public String getPassword() {
        return password;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public AnnounceAckAckPacket getAckAck(){
        return new AnnounceAckAckPacket();
    }
    public AnnounceAckPacket(String nickName, String password, long timestamp) {
        this.nickName = nickName;
        this.password = password;
        this.timestamp = timestamp;
    }

    public static AnnounceAckPacket parse(byte[] data) throws InvalidPacketFormatException{
        ByteBuffer buff = ByteBuffer.wrap(data);
        if (buff.remaining()<15){
            throw new InvalidPacketFormatException("Received invalid ANNOUNCE packet.");
        }

        byte nlength = buff.get();
        if (nlength <= 0 || nlength > 32 || nlength > buff.remaining() -12) {
            throw new InvalidPacketFormatException("Received invalid ANNOUNCE packet.");
        }

        byte[] name = new byte[nlength];
        buff.get(name);

        int plength = buff.get();
        if(plength <=0 || plength > 512 || plength > buff.remaining() -8 ){
            throw new InvalidPacketFormatException("Received invalid ANNOUNCE packet.");

        }
        byte[] pass = new byte[plength];
        buff.get(pass);

        long timestamp = buff.getLong();

        return new AnnounceAckPacket(new String(name,StandardCharsets.UTF_8),new String(pass,StandardCharsets.UTF_8),timestamp);


    }

    @Override
    byte[] serialize() {
        byte[] name = this.nickName.getBytes(StandardCharsets.UTF_8);
        byte[] password = this.password.getBytes(StandardCharsets.UTF_8);

        ByteBuffer buff = ByteBuffer.allocate(name.length + password.length + 10);

        buff.put((byte) name.length);
        buff.put(name);
        buff.put((byte) password.length);
        buff.put(password);
        buff.putLong(timestamp);
        return buff.array();
    }

    @Override
    public byte getOperationCode() {
        return Constants.OPCODE.ANNACK;
    }
    public boolean equals(Object other) {
        if (!(other instanceof MessagePacket)) return false;
        AnnounceAckPacket o = (AnnounceAckPacket) other;
        return o.nickName.equals(this.nickName) && o.password.equals(this.password) && o.timestamp==this.timestamp;
    }
}
/*
          1           <= 32       4           <=512         8

     | nickLength | nickname |  passLength | password |  timestamp |

 */
