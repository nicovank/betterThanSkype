package Client.Sockets;
import Client.Connectables.Message;
import utils.Address;

public interface RoomSocket {

    boolean sendToEveryone(String message,long timeStamp);

    boolean sendToSingle(String message, String recipient, long timeStamp);

    Message receive();

    boolean sendToServer(String message);

    String receiveFromServer();

    void addToSendList(String nickName,Address address);

    void removeFromSendList(String nickName);

}
