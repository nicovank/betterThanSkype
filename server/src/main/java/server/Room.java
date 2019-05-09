package server;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Room {

    private String secret;
    private Map<String, Peer> peers;
    private String password;
    private String name;


    public Room(HashMap<String, Peer> PS, String PW, String NN) {
        peers = PS;
        name = NN;
        password = PW;
        secret = PW;

        peers = new ConcurrentHashMap<>();
    }

    //AddPeer, unique by the NickName
    public boolean addPeer(Peer p) {

        peers.put(p.getNickname(), p);
        return true;

    }

    //RemovePeer by the NickName
    public boolean removePeer(String nn) {
        peers.remove(nn);
        return true;
    }

    //GetPeer
    public Peer getPeer(String nn) {
        return peers.get(nn);
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getSecret() {
        return secret;
    }
}
