package Client.Events;

import javafx.event.Event;
import javafx.event.EventType;

public class MessageReceivedEvent extends Event {
    private Message message;
    public MessageReceivedEvent(EventType<? extends Event> eventType,Message message) {
        super(eventType);
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
