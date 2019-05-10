package rooms;

import java.net.InetAddress;

public final class MulticastRoom extends Room {
    private final InetAddress ip;
    private final long port;

    public MulticastRoom(String name, String password, InetAddress ip, long port) {
        super(name, password);
        this.ip = ip;
        this.port = port;
    }

    public InetAddress getIP() {
        return ip;
    }

    public long getPort() {
        return port;
    }
}
