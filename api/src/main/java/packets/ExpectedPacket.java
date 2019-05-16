package packets;

import java.util.Objects;

/**
 * Data structure for dealing with timeouts, contains a reference to the expected next packet, the original packet, time, and timestmap
 * @author Nick Esposito
 *
 */
public class ExpectedPacket {
    private long time;
    private long timestamp;
    private Packet packet;

    public Packet getOriginal() {
        return original;
    }

    private Packet original;

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Packet getPacket() {
        return packet;
    }

    public ExpectedPacket(Packet packet, long timestamp, Packet original) {
        this.packet = packet;
        this.timestamp = timestamp;
        this.original = original;
        time = System.nanoTime();
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, timestamp, packet);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ExpectedPacket)) return false;
        ExpectedPacket o = (ExpectedPacket) other;
        return o.time == this.time && o.timestamp == this.timestamp && packet.equals(o.packet) && original.equals(o.original);
    }
}
