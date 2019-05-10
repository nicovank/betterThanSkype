package packets;

import utils.Constants;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class SuccessfulRoomCreationPacket extends Packet {

    private final String name;
    private final String password;
    private final InetAddress ip;
    private final int port;
    private final byte type;

    public SuccessfulRoomCreationPacket(String name, String password, InetAddress ip, int port, byte type) {
        this.name = name;
        this.password = password;
        this.ip = ip;
        this.port = port;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public byte getType() { return type; }

    public static SuccessfulRoomCreationPacket parse(byte[] data) throws InvalidPacketFormatException {
        ByteBuffer buff = ByteBuffer.wrap(data);

        if (data.length == 0) {
            throw new InvalidPacketFormatException("Received invalid CRSUC packet.");
        }

        byte nlength = buff.get();
        if (nlength > buff.remaining() - 7 || nlength > 32 || nlength <= 0) {
            throw new InvalidPacketFormatException("Received invalid CRSUC packet.");
        }
        byte [] name = new byte[nlength];
        buff.get(name);

        byte pwlength = buff.get();
        if (pwlength > buff.remaining() - 6 || pwlength > 32 || pwlength <= 0) {
            throw new InvalidPacketFormatException("Received invalid CRSUC packet.");
        }
        byte [] password = new byte[pwlength];
        buff.get(password);

        byte iplength = buff.get();
        if (pwlength > buff.remaining() - 5 || pwlength > 32 || pwlength <= 0) {
            throw new InvalidPacketFormatException("Received invalid CRSUC packet.");
        }
        byte [] ip = new byte[iplength];
        buff.get(ip);

        int port = buff.getInt();

        byte type = buff.get();

        try {
            return new SuccessfulRoomCreationPacket(
                    new String(name, StandardCharsets.UTF_8),
                    new String(password, StandardCharsets.UTF_8),
                    InetAddress.getByAddress(ip),
                    port,
                    type
            );
        } catch (UnknownHostException e) {
            throw new InvalidPacketFormatException("The IP received was not valid.");
        }
    }

    @Override
    byte[] serialize() {
        byte[] name = this.name.getBytes(StandardCharsets.UTF_8);
        byte[] password = this.password.getBytes(StandardCharsets.UTF_8);
        byte[] ip = this.ip.getAddress();

        byte[] packet = new byte[name.length + password.length + ip.length + 8];
        ByteBuffer buffer = ByteBuffer.wrap(packet);
        buffer.put((byte)name.length);
        buffer.put(name);
        buffer.put((byte)password.length);
        buffer.put(password);
        buffer.put((byte)ip.length);
        buffer.put(ip);
        buffer.putInt(port);
        buffer.put(type);

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
