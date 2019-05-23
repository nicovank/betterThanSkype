package packets;

import utils.Constants;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A packet for requesting to create a room.
 * @throws InvalidPacketFormatException if trying to parse an invalid packet
 * @author Mike Doran
 */

public class RoomCreationRequestPacket extends Packet {

    private final String userName;
    private final String roomName;
    private final String password;
    private final byte type;

    public String getUserName() {
        return userName;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getPassword() {
        return password;
    }

    public byte getType() {
        return type;
    }

    public RoomCreationRequestPacket(String userName, String roomName, String password, byte type) {
        this.userName = userName;
        this.roomName = roomName;
        this.password = password;
        this.type = type;
    }

    public static RoomCreationRequestPacket parse(byte[] data) throws InvalidPacketFormatException {
        ByteBuffer buffer = ByteBuffer.wrap(data);


        if (!buffer.hasRemaining()) throw new InvalidPacketFormatException("Received invalid CREATEROOM packet.");
        byte nlength = buffer.get();
        if (nlength > buffer.remaining() || nlength <= 0 || nlength > 32)
            throw new InvalidPacketFormatException("Received invalid CREATEROOM packet.");
        byte[] userName = new byte[nlength];
        buffer.get(userName);


        if (!buffer.hasRemaining()) throw new InvalidPacketFormatException("Received invalid CREATEROOM packet.");
        byte rnlength = buffer.get();
        if (rnlength > buffer.remaining() || rnlength <= 0 || rnlength > 32)
            throw new InvalidPacketFormatException("Received invalid CREATEROOM packet.");
        byte[] roomName = new byte[rnlength];
        buffer.get(roomName);


        if (buffer.remaining() < 4) throw new InvalidPacketFormatException("Received invalid CREATEROOM packet.");
        int plength = buffer.getInt();
        if (plength > buffer.remaining() || plength < 0 || plength > 512)
            throw new InvalidPacketFormatException("Received invalid CREATEROOM packet.");
        byte[] password = new byte[plength];
        buffer.get(password);


        if (buffer.remaining() != 1) throw new InvalidPacketFormatException("Received invalid CREATEROOM packet.");
        byte type = buffer.get();



        return new RoomCreationRequestPacket(
                new String(userName, StandardCharsets.UTF_8),
                new String(roomName, StandardCharsets.UTF_8),
                new String(password, StandardCharsets.UTF_8),
                type
        );
    }

    @Override
    byte[] serialize() {
        byte[] userName = this.userName.getBytes(StandardCharsets.UTF_8);
        byte[] roomName = this.roomName.getBytes(StandardCharsets.UTF_8);
        byte[] password = this.password.getBytes(StandardCharsets.UTF_8);

        ByteBuffer buffer = ByteBuffer.allocate(userName.length + roomName.length + password.length + 7);

        buffer.put((byte) userName.length);
        buffer.put(userName);
        buffer.put((byte) roomName.length);
        buffer.put(roomName);
        buffer.putInt(password.length);
        buffer.put(password);
        buffer.put(this.type);

        return buffer.array();
    }

    @Override
    public byte getOperationCode() {
        return Constants.OPCODE.CREATEROOM;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, roomName, password, type);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof RoomCreationRequestPacket)) return false;
        RoomCreationRequestPacket o = (RoomCreationRequestPacket) other;
        return o.userName.equals(this.userName)
                && o.roomName.equals(this.roomName)
                && o.password.equals(this.password)
                && o.type == this.type;
    }
}
