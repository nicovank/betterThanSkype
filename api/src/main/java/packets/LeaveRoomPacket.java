package packets;

import utils.Constants;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class LeaveRoomPacket extends Packet {
    private final String nickname;

    public LeaveRoomPacket(String nickname) {
        this.nickname = nickname;
    }

    public static LeaveRoomPacket parse(byte[] data) throws InvalidPacketFormatException{
        ByteBuffer buff = ByteBuffer.wrap(data);
        byte nlength = buff.get();
        if (buff.remaining()<1){
            throw new InvalidPacketFormatException("Received invalid LEAVEROOM packet");
        }
        byte[] name = new byte[nlength];
        buff.get(name);
        return new LeaveRoomPacket(new String(name, StandardCharsets.UTF_8));
    }
    @Override
    byte[] serialize() {
        byte[] name = nickname.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buff = ByteBuffer.allocate(name.length+1);

        buff.put((byte) name.length);
        buff.put(name);
        return buff.array();
    }

    @Override
    public byte getOperationCode() {
        return Constants.OPCODE.LEAVEROOM;
    }
}
/*
          1           <= 32

     | nickLength | nickname |

 */