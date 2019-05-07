package packets;

import utils.Constants;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class LeaveRoomPacket extends Packet {
    private final String nickname;
    private final String roomname;

    public LeaveRoomPacket(String nickname, String roomname) {
        this.nickname = nickname;
        this.roomname = roomname;
    }

    public static LeaveRoomPacket parse(byte[] data) throws InvalidPacketFormatException{
        ByteBuffer buff = ByteBuffer.wrap(data);

        byte nlength = buff.get();
        if (buff.remaining()<1){
            throw new InvalidPacketFormatException("Received invalid LEAVEROOM packet");
        }
        byte[] nickname = new byte[nlength];
        buff.get(nickname);

        byte rnlength = buff.get();
        byte[] roomname = new byte[rnlength];
        buff.get(roomname);

        return new LeaveRoomPacket(new String(nickname, StandardCharsets.UTF_8),
                                   new String(roomname, StandardCharsets.UTF_8));
    }

    @Override
    byte[] serialize() {
        byte[] nickname = this.nickname.getBytes(StandardCharsets.UTF_8);
        byte[] roomname = this.roomname.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buff = ByteBuffer.allocate(roomname.length + nickname.length + 2);

        buff.put((byte) nickname.length);
        buff.put(nickname);
        buff.put((byte) roomname.length);
        buff.put(roomname);
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