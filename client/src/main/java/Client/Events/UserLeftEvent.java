package Client.Events;

import javafx.event.Event;
import javafx.event.EventType;

public class UserLeftEvent extends Event {
    public static final EventType<UserLeftEvent> LEAVE_EVENT = new EventType<>(ANY);
    private String username;
    public UserLeftEvent(EventType<? extends Event> eventType, String username) {
        super(eventType);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
