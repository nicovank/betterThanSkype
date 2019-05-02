package packets;

import crypto.CryptoException;
import crypto.RSA;
import utils.Constants;

import java.nio.ByteBuffer;
import java.security.PublicKey;
import java.util.Arrays;

public final class PublicKeyRequestPacket extends Packet {
    private final byte[] pub;

    public PublicKeyRequestPacket(byte[] pub) {
        this.pub = pub;
    }

    public PublicKeyRequestPacket(PublicKey pub) {
        this(pub.getEncoded());
    }

    public PublicKey getPublicKey() throws CryptoException {
        return RSA.decodePublicKey(pub);
    }

    @Override
    byte[] serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(pub.length + 4);
        buffer.putInt(pub.length);
        buffer.put(pub);
        return buffer.array();
    }

    @Override
    public byte getOperationCode() {
        return Constants.OPCODE.PUBREQ;
    }

    public static PublicKeyRequestPacket parse(byte[] data) throws InvalidPacketFormatException {
        ByteBuffer buffer = ByteBuffer.wrap(data);

        if (buffer.remaining() < 4) {
            throw new InvalidPacketFormatException("Received invalid PUBREQ packet.");
        }

        int length = buffer.getInt();

        if (length > buffer.remaining() || length < 0 || length > 900) {
            throw new InvalidPacketFormatException("Received invalid PUBREQ packet.");
        }

        byte[] pub = new byte[length];
        buffer.get(pub);
        return new PublicKeyRequestPacket(pub);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(pub);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof PublicKeyRequestPacket && Arrays.equals(((PublicKeyRequestPacket) other).pub, this.pub);
    }
}
