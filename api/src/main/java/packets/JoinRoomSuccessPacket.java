package packets;

import utils.Constants;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class JoinRoomSuccessPacket extends Packet {

    private final String name;
    private final String secret;
    private final InetAddress ip;
    private final int port;
    private final byte type;

    public JoinRoomSuccessPacket(String name, String secret, InetAddress ip, int port, byte type) {
        this.name = name;
        this.secret = secret;
        this.ip = ip;
        this.port = port;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return secret;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public byte getType() {
        return type;
    }

    public static JoinRoomSuccessPacket parse(byte[] data) throws InvalidPacketFormatException {
        ByteBuffer buffer = ByteBuffer.wrap(data);


        if (!buffer.hasRemaining()) throw new InvalidPacketFormatException("Received invalid JOINSUC packet.");
        byte nlength = buffer.get();
        if (nlength > buffer.remaining() || nlength <= 0 || nlength > 32)
            throw new InvalidPacketFormatException("Received invalid JOINSUC packet.");
        byte[] name = new byte[nlength];
        buffer.get(name);


        if (buffer.remaining() < 4) throw new InvalidPacketFormatException("Received invalid JOINSUC packet.");
        int slength = buffer.getInt();
        if (slength > buffer.remaining() || slength < 0 || slength > 512)
            throw new InvalidPacketFormatException("Received invalid JOINSUC packet.");
        byte[] secret = new byte[slength];
        buffer.get(secret);


        if (!buffer.hasRemaining()) throw new InvalidPacketFormatException("Received invalid JOINSUC packet.");
        byte type = buffer.get();


        if (!buffer.hasRemaining()) throw new InvalidPacketFormatException("Received invalid JOINSUC packet.");
        byte iplength = buffer.get();
        if (iplength > buffer.remaining() || iplength <= 0)
            throw new InvalidPacketFormatException("Received invalid JOINSUC packet.");
        byte[] ip = new byte[iplength];
        buffer.get(ip);


        if (buffer.remaining() != 4) throw new InvalidPacketFormatException("Received invalid JOINSUC packet.");
        int port = buffer.getInt();



        try {
            return new JoinRoomSuccessPacket(
                    new String(name, StandardCharsets.UTF_8),
                    new String(secret, StandardCharsets.UTF_8),
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
        byte[] secret = this.secret.getBytes(StandardCharsets.UTF_8);
        byte[] ip = this.ip.getAddress();

        byte[] packet = new byte[name.length + secret.length + ip.length + 11];
        ByteBuffer buffer = ByteBuffer.wrap(packet);
        buffer.put((byte) name.length);
        buffer.put(name);
        buffer.putInt(secret.length);
        buffer.put(secret);
        buffer.put(type);
        buffer.put((byte) ip.length);
        buffer.put(ip);
        buffer.putInt(port);

        return packet;
    }

    @Override
    public byte getOperationCode() {
        return Constants.OPCODE.JOINSUC;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, secret, type, ip, port);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof JoinRoomSuccessPacket)) return false;
        JoinRoomSuccessPacket o = (JoinRoomSuccessPacket) other;
        return o.name.equals(this.name)
                && o.secret.equals(this.secret)
                && o.type == this.type
                && o.ip.equals(this.ip)
                && o.port == this.port;
    }
}
