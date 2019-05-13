package Client.Events;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * this class represents a message event for use with custom javaFX events.
 * @author Jim Spagnola
 */
public class MessageReceivedEvent extends Event {
    private Message message;
    public static final EventType<MessageReceivedEvent> MESSAGE_EVENT = new EventType<>("MESSAGE_EVENT");
    public MessageReceivedEvent(EventType<? extends Event> eventType,Message message) {
        super(eventType);
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
