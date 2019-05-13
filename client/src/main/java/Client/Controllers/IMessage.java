package Client.Controllers;

import Client.Events.MessageReceivedEvent;
/**
 * This interface requires methods that respond and send messages
 * @author Jim Spagnola
 */
public interface IMessage {
    void sendMessage(String message);
    void receiveMessage(MessageReceivedEvent event);
}
