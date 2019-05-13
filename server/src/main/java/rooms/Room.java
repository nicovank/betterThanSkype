package rooms;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Room {

    private final String name;
    private final String password;
    private final String secret;
    private final Map<String, Peer> peers;


    public Room(String name, String password) {
        this.name = name;
        this.password = password;
        this.secret = password;

        peers = new ConcurrentHashMap<>();
    }

    public abstract byte getType();

    /**
     * Adds a new peer to the room, provided it does not contain a peer with that nickname yet.
     *
     * @param p the peer to add to the room.
     * @return true if the add was successful, else false.
     */
    public boolean addPeer(Peer p) {
        Peer q = peers.putIfAbsent(p.getNickname(), p);
        return p.equals(q);
    }

    /**
     * Removes a peer with the given nickname. Returns whether a value was removed or not.
     *
     * @param nickname the nickname of the peer to remove.
     * @return true if a value was removed, else false.
     */
    public boolean removePeer(String nickname) {
        return peers.remove(nickname) != null;
    }

    /**
     * Find peer information from a given nickname.
     *
     * @param nickname the nickname of the peer to find information for.
     * @return a Peer, or null if we have no information on the peer.
     */
    public Peer getPeer(String nickname) {
        return peers.get(nickname);
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

    @Override
    public int hashCode() {
        return Objects.hash(name, password, secret, peers);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Room)) return false;
        Room room = (Room) other;
        return room.name.equals(this.name);
    }
}
