package Client.Events;

import javafx.event.Event;
import javafx.event.EventType;

public class UserJoinedEvent extends Event {
    private String username;
    public UserJoinedEvent(EventType<? extends Event> eventType, String username) {
        super(eventType);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
