package Client.Events;

import javafx.event.EventType;
import javafx.event.Event;

public class RoomResponseEvent extends Event {
    public static final EventType<RoomResponseEvent> JOIN_ROOM = new EventType<>(ANY);
    public static final EventType<RoomResponseEvent> CREATE_ROOM = new EventType<>(ANY);
    private final boolean response;
    public RoomResponseEvent(EventType<? extends Event> eventType, boolean response) {
        super(eventType);
        this.response = response;
    }

    public boolean getResponse(){
        return response;
    }
}
