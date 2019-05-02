package Client.Controllers;

public interface IMessage {
    boolean sendMessage(String message);
    void receiveMessage();
}
