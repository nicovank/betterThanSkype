package Client.Events;

import javafx.event.Event;
import javafx.event.EventType;

public class UserLeftEvent extends Event {
    private String username;
    public UserLeftEvent(EventType<? extends Event> eventType, String username) {
        super(eventType);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
