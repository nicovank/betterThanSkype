package Client.Sockets;
import utils.Address;

public interface IRoomSocket {
    void attemptToCreateRoom(String room, String username, String password);

    void attemptToJoinRoom(String room, String username, String password);

    long sendToEveryone(String message,String password);

    void sendLeavingMessage(String username, String roomname);

    void addToSendList(String nickName,Address address);

    void removeFromSendList(String nickName);

}
