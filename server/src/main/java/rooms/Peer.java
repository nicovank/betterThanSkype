package rooms;

import utils.Address;

import java.util.Objects;

/**
 * Class to keep track of each connection to a room. Will hold the nickname, address
 * @author Michael Anthony
 */

public final class Peer {
    private final String nickname;
    private final Address address;

    public Peer(String nickname, Address address) {
        this.nickname = nickname;
        this.address = address;
    }

    /**
     * Get Nickname of the Peer
     *
     * @return the Peer's NickName
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Get name of a room
     *
     * @return a Rooms name
     */
    public Address getAddress() {
        return address;
    }


    /**
     * Used to get the hashcode for the Peer
     *
     * @return a Peer's hashcode is based off the unique Nickname for the Room
     */
    @Override
    public int hashCode() {
        return nickname.hashCode();
    }

    /**
     * Used to see if Peer is equal to another Peer object
     * @param other (has to be a Peer object)
     * @return false if the object is not a Peer or if the Unique Nickname does not equal.  True if it is a Peer and NickName is equivalent
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof Peer && ((Peer) other).nickname.equals(this.nickname);
    }
}
