package packets;

import utils.Constants;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class AnnouncePacket extends Packet {
    private final String nickName;
    private final String password;

    public AnnouncePacket(String nickName, String password){
        this.nickName = nickName;
        this.password = password;
    }

    public static AnnouncePacket parse(byte[] data) throws InvalidPacketFormatException{
        ByteBuffer buff = ByteBuffer.wrap(data);
        if (buff.remaining()<8){
            throw new InvalidPacketFormatException("Received invalid ANNOUNCE packet.");
        }

        byte nlength = buff.get();
        if (nlength <= 0 || nlength > 32 || nlength > buff.remaining() - 5) {
            throw new InvalidPacketFormatException("Received invalid ANNOUNCE packet.");
        }

        byte[] name = new byte[nlength];
        buff.get(name);

        int plength = buff.getInt();
        if(plength <=0 || plength > 512 || plength > buff.remaining() -1 ){
            throw new InvalidPacketFormatException("Received invalid ANNOUNCE packet.");

        }
        byte[] pass = new byte[plength];
        buff.get(pass);

        return new AnnouncePacket(new String(name, StandardCharsets.UTF_8),new String(pass,StandardCharsets.UTF_8));

    }
    @Override
    byte[] serialize() {
        byte[] name = this.nickName.getBytes(StandardCharsets.UTF_8);
        byte[] password = this.password.getBytes(StandardCharsets.UTF_8);

        ByteBuffer buff = ByteBuffer.allocate(name.length + password.length + 5);

        buff.put((byte) name.length);
        buff.put(name);
        buff.put((byte) password.length);
        buff.put(password);

        return buff.array();
    }

    public AnnounceAckPacket createAck(long timestamp){
        return new AnnounceAckPacket(nickName,password,timestamp);
    }

    @Override
    public byte getOperationCode() {
        return Constants.OPCODE.ANNOUNCE;
    }
    @Override
    public int hashCode() {
        return Objects.hash(nickName, password);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof AnnouncePacket)) return false;
        AnnouncePacket o = (AnnouncePacket) other;
        return o.nickName.equals(this.nickName) && o.password.equals(this.password);
    }

    public String getNickName() {
        return nickName;
    }
}


/*
          1           <= 32       4           <=512

     | nickLength | nickname |  passLength | password |

 */