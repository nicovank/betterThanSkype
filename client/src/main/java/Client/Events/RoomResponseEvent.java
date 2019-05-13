package Client.Events;

import javafx.event.EventType;
import javafx.event.Event;

/**
 * this class represents a room Response event for use with custom javaFX events.
 * @author Jim Spagnola
 */
public class RoomResponseEvent extends Event {
    public static final EventType<RoomResponseEvent> JOIN_ROOM = new EventType<>(ANY);
    public static final EventType<RoomResponseEvent> CREATE_ROOM = new EventType<>(ANY);
    private final boolean response;
    private final String room;
    public RoomResponseEvent(EventType<? extends Event> eventType, boolean response,String room) {
        super(eventType);
        this.response = response;
        this.room = room;
    }

    public String getRoom() {
        return room;
    }

    public boolean getResponse(){
        return response;
    }
}
