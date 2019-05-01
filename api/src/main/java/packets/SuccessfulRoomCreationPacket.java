package packets;

import utils.Constants;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class SuccessfulRoomCreationPacket extends Packet {

    private final String name;

    public SuccessfulRoomCreationPacket(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SuccessfulRoomCreationPacket parse(byte[] data) throws InvalidPacketFormatException {
        if (data.length == 0) {
            throw new InvalidPacketFormatException("Received invalid CRSUC packet.");
        }

        byte length = data[0];
        if (length > data.length - 1 || length > 32 || length <= 0) {
            throw new InvalidPacketFormatException("Received invalid CRSUC packet.");
        }

        return new SuccessfulRoomCreationPacket(new String(Arrays.copyOfRange(data, 1, length + 1), StandardCharsets.UTF_8));
    }

    @Override
    byte[] serialize() {
        byte[] name = this.name.getBytes(StandardCharsets.UTF_8);
        byte[] packet = new byte[name.length + 1];
        packet[0] = (byte) name.length;
        System.arraycopy(name, 0, packet, 1, name.length);
        return packet;
    }

    @Override
    public byte getOperationCode() {
        return Constants.OPCODE.CRSUC;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof SuccessfulRoomCreationPacket && ((SuccessfulRoomCreationPacket) other).name.equals(this.name);
    }
}
