package packets;

import utils.Constants;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class RoomCreationRequestPacket extends Packet {

    private final String name;
    private final String password;
    private final byte type;

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public byte getType() {
        return type;
    }

    public RoomCreationRequestPacket(String name, String password, byte type) {
        this.name = name;
        this.password = password;
        this.type = type;
    }

    public static RoomCreationRequestPacket parse(byte[] data) throws InvalidPacketFormatException {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        if (buffer.remaining() < 8) {
            throw new InvalidPacketFormatException("Received invalid CREATEROOM packet.");
        }

        byte nlength = buffer.get();
        if (nlength <= 0 || nlength > 32 || nlength > buffer.remaining() - 5) {
            throw new InvalidPacketFormatException("Received invalid CREATEROOM packet.");
        }

        byte[] name = new byte[nlength];
        buffer.get(name);

        int plength = buffer.getInt();
        if (plength < 0 || plength > 512 || plength > buffer.remaining() - 1) {
            throw new InvalidPacketFormatException("Received invalid CREATEROOM packet.");
        }

        byte[] password = new byte[plength];
        buffer.get(password);

        byte type = buffer.get();

        return new RoomCreationRequestPacket(
                new String(name, StandardCharsets.UTF_8),
                new String(password, StandardCharsets.UTF_8),
                type
        );
    }

    @Override
    byte[] serialize() {
        byte[] name = this.name.getBytes(StandardCharsets.UTF_8);
        byte[] password = this.password.getBytes(StandardCharsets.UTF_8);

        ByteBuffer buffer = ByteBuffer.allocate(name.length + password.length + 6);

        buffer.put((byte) name.length);
        buffer.put(name);
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
        return Objects.hash(name, password, type);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof RoomCreationRequestPacket)) return false;
        RoomCreationRequestPacket o = (RoomCreationRequestPacket) other;
        return o.name.equals(this.name) && o.password.equals(this.password) && o.type == this.type;
    }
}
