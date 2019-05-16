package rooms;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keep track of Rooms where a certain group of clients can connect and leave from
 * @author Michael Anthony
 */


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

    public boolean isEmpty() {
        return peers.isEmpty();
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

    /**
     * Get name of a room
     *
     * @return a Rooms name
     */
    public String getName() {
        return name;
    }

    /**
     * Get password for a room
     *
     * @return Room's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get name of a room
     *
     * @return a Rooms name
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Used to get the hashcode for the Room
     *
     * @return a Rooms hashcode based off the parameters for the Room
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, password, secret, peers);
    }

    /**
     * Used to see if Room is equal to another Room object
     * @param other (has to be a room object)
     * @return false if the object is not a Room. True if it is a Room and has all require parameters
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Room)) return false;
        Room room = (Room) other;
        return room.name.equals(this.name);
    }
}
