package Client.Events;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * this class represents a message event for use with custom javaFX events.
 * @author Jim Spagnola
 */
public class UserEvent extends Event {
    public static final EventType<UserEvent> JOIN_EVENT = new EventType<>(ANY);
    public static final EventType<UserEvent> LEAVE_EVENT = new EventType<>(ANY);
    private String username;
    public UserEvent(EventType<? extends Event> eventType, String username) {
        super(eventType);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
