package Client.Sockets;
import Client.Connectables.Message;
import utils.Address;

public interface IRoomSocket {
    void attemptToCreateRoom(String room, String username, String password);

    void attemptToJoinRoom(String room, String username, String password);

    void sendToEveryone(String message,String password);

    void addToSendList(String nickName,Address address);

    void removeFromSendList(String nickName);

}
