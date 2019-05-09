package server;

import java.net.InetAddress;
import java.util.HashMap;

public final class MulticastRoom extends Room {
    private InetAddress ip;
    private long port;

    public MulticastRoom(HashMap<String, Peer> PS, String PW, String NN, InetAddress ip, long p) {
        super(PS, PW, NN);
        this.ip = ip;
        this.port = p;
    }
}
