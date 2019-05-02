package packets;

import utils.Constants;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class ErrorPacket extends Packet {

    private final byte code;
    private final String message;

    public ErrorPacket(byte code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorPacket(byte code) {
        this(code, null);
    }

    public byte getErrorCode() {
        return code;
    }

    public boolean hasMessage() {
        return message != null;
    }

    public String getMessage() {
        return message;
    }

    public static ErrorPacket parse(byte[] data) throws InvalidPacketFormatException {

        ByteBuffer buffer = ByteBuffer.wrap(data);

        if (buffer.remaining() < 5) {
            throw new InvalidPacketFormatException("Received invalid ERROR packet.");
        }

        byte code = buffer.get();
        int length = buffer.getInt();

        if (length == -1) {
            return new ErrorPacket(code);
        }

        if (length > buffer.remaining() || length < 0 || length > 900) {
            throw new InvalidPacketFormatException("Received invalid ERROR packet.");
        }

        byte[] message = new byte[length];
        buffer.get(message);
        return new ErrorPacket(code, new String(message, StandardCharsets.UTF_8));
    }

    @Override
    byte[] serialize() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(code);

        if (this.hasMessage()) {
            byte[] message = this.message.getBytes(StandardCharsets.UTF_8);
            out.writeBytes(ByteBuffer.allocate(4).putInt(message.length).array());
            out.writeBytes(message);
        } else {
            out.writeBytes(new byte[]{-1, -1, -1, -1});
        }

        return out.toByteArray();
    }

    @Override
    public byte getOperationCode() {
        return Constants.OPCODE.ERROR;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ErrorPacket && ((ErrorPacket) other).code == this.code && ((ErrorPacket) other).message.equals(this.message);
    }
}
