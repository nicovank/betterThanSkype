package packets;

import crypto.CryptoException;
import crypto.RSA;
import utils.Constants;

import java.nio.ByteBuffer;
import java.security.PublicKey;
import java.util.Arrays;

public final class PublicKeyPacket extends Packet {
    private final byte[] pub;

    public PublicKeyPacket(byte[] pub) {
        this.pub = pub;
    }

    public PublicKeyPacket(PublicKey pub) {
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
        return Constants.OPCODE.PUB;
    }

    public static PublicKeyPacket parse(byte[] data) throws InvalidPacketFormatException {
        ByteBuffer buffer = ByteBuffer.wrap(data);


        if (buffer.remaining() < 4) throw new InvalidPacketFormatException("Received invalid PUB packet.");
        int length = buffer.getInt();
        if (length != buffer.remaining()) throw new InvalidPacketFormatException("Received invalid PUB packet.");
        byte[] pub = new byte[length];
        buffer.get(pub);



        return new PublicKeyPacket(pub);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(pub);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof PublicKeyPacket && Arrays.equals(((PublicKeyPacket) other).pub, this.pub);
    }
}
