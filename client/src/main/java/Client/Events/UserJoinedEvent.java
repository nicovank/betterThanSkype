package Client.Events;

import javafx.event.Event;
import javafx.event.EventType;

public class UserJoinedEvent extends Event {
    public static final EventType<UserJoinedEvent> JOIN_EVENT = new EventType<>(ANY);
    private String username;
    public UserJoinedEvent(EventType<? extends Event> eventType, String username) {
        super(eventType);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
