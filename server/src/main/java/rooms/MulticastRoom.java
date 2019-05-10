package rooms;

import utils.Constants;

import java.net.InetAddress;

public final class MulticastRoom extends Room {
    private final InetAddress ip;
    private final int port;

    public MulticastRoom(String name, String password, InetAddress ip, int port) {
        super(name, password);
        this.ip = ip;
        this.port = port;
    }

    public InetAddress getIP() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public byte getType() {
        return Constants.TYPE.MULTICAST;
    }
}
