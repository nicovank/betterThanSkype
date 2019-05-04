package Client.Controllers;

import Client.Events.MessageReceivedEvent;

public interface IMessage {
    void sendMessage(String message);
    void receiveMessage(MessageReceivedEvent event);
}
