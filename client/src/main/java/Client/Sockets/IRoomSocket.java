package Client.Sockets;
import utils.Address;

/**
 * this interface is requires methods needed to connect to a room server and communicate
 * client to client.
 * @author Jim Spagnola
 */
public interface IRoomSocket {

    void attemptToCreateRoom(String room, String username, String password);
    void attemptToJoinRoom(String username, String room, String password);
    long sendToEveryone(String username, String message,String password);
    void sendLeavingMessage(String username, String roomname);
    void addToSendList(String nickName,Address address);
    void removeFromSendList(String nickName);
    void announce(String username, String password);

}
