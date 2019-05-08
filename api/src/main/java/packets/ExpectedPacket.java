package packets;

public class ExpectedPacket {
    private long time;
    private long timestamp;
    private Packet packet;

    public long getTime() {
        return time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Packet getPacket() {
        return packet;
    }

    public ExpectedPacket(Packet packet,long timestamp ){
        this.packet=packet;
        this.timestamp = timestamp;
        time=System.nanoTime();
    }
}
