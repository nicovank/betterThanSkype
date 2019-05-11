package packets;

import utils.Constants;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class SuccessfulMulticastRoomCreationPacket extends Packet {

    private final String name;
    private final String secret;
    private final InetAddress ip;
    private final int port;

    public SuccessfulMulticastRoomCreationPacket(String name, String secret, InetAddress ip, int port) {
        this.name = name;
        this.secret = secret;
        this.ip = ip;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public String getSecret() {
        return secret;
    }

    public InetAddress getIP() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public static SuccessfulMulticastRoomCreationPacket parse(byte[] data) throws InvalidPacketFormatException {
        ByteBuffer buffer = ByteBuffer.wrap(data);


        if (!buffer.hasRemaining()) throw new InvalidPacketFormatException("Received invalid MRCS packet.");
        byte nlength = buffer.get();
        if (nlength > buffer.remaining() || nlength <= 0 || nlength > 32)
            throw new InvalidPacketFormatException("Received invalid MRCS packet.");
        byte[] name = new byte[nlength];
        buffer.get(name);


        if (buffer.remaining() < 4) throw new InvalidPacketFormatException("Received invalid MRCS packet.");
        int slength = buffer.getInt();
        if (slength > buffer.remaining() || slength > 512 || slength < 0)
            throw new InvalidPacketFormatException("Received invalid MRCS packet.");
        byte[] secret = new byte[slength];
        buffer.get(secret);


        if (!buffer.hasRemaining()) throw new InvalidPacketFormatException("Received invalid MRCS packet.");
        byte iplength = buffer.get();
        if (iplength > buffer.remaining() || iplength <= 0) throw new InvalidPacketFormatException("Received invalid MRCS packet.");
        byte[] ip = new byte[iplength];
        buffer.get(ip);


        if (buffer.remaining() < 4) throw new InvalidPacketFormatException("Received invalid MRCS packet.");
        int port = buffer.getInt();


        try {
            return new SuccessfulMulticastRoomCreationPacket(
                    new String(name, StandardCharsets.UTF_8),
                    new String(secret, StandardCharsets.UTF_8),
                    InetAddress.getByAddress(ip),
                    port
            );
        } catch (UnknownHostException e) {
            throw new InvalidPacketFormatException("Received invalid MRCS packet.");
        }
    }

    @Override
    byte[] serialize() {
        byte[] name = this.name.getBytes(StandardCharsets.UTF_8);
        byte[] secret = this.secret.getBytes(StandardCharsets.UTF_8);
        byte[] ip = this.ip.getAddress();

        byte[] packet = new byte[name.length + secret.length + ip.length + 10];
        ByteBuffer buffer = ByteBuffer.wrap(packet);
        buffer.put((byte) name.length);
        buffer.put(name);
        buffer.putInt(secret.length);
        buffer.put(secret);
        buffer.put((byte) ip.length);
        buffer.put(ip);
        buffer.putInt(port);

        return packet;
    }

    @Override
    public byte getOperationCode() {
        return Constants.OPCODE.MRCS;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof SuccessfulMulticastRoomCreationPacket && ((SuccessfulMulticastRoomCreationPacket) other).name.equals(this.name);
    }
}
