package Client.Sockets;
import packets.*;
import utils.Address;

public interface RoomSocket {

    void sendToEveryone(Packet packet);

    void sendToSingle(Packet packet, Address address);

    Packet receive();

    void sendToServer(Packet packet);

    Packet receiveFromServer();

    void addToSendList(String nickName,Address address);

    void removeFromSendList(String nickName);

}
