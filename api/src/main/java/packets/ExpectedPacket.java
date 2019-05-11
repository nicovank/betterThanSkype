package packets;

public class ExpectedPacket {
    private long time;
    private long timestamp;
    private Packet packet;

    public Packet getOriginal() {
        return original;
    }

    private Packet original;

    public long getTime() {
        return time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Packet getPacket() {
        return packet;
    }

    public ExpectedPacket(Packet packet,long timestamp,Packet original){
        this.packet=packet;
        this.timestamp = timestamp;
        this.original = original;
        time=System.nanoTime();
    }
}
