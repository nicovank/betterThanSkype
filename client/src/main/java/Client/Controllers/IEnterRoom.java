package Client.Controllers;

import Client.Events.RoomResponseEvent;
import javafx.scene.input.MouseEvent;

/**
 * This interface requires methods that respond and send information pertaining to a Room
 * @author Jim Spagnola
 */
public interface IEnterRoom {

    void onCreateRoom(MouseEvent e);
    void onJoinRoom(MouseEvent e);
    void onNewRoomResponse(RoomResponseEvent e);
    void onJoinRoomResponse(RoomResponseEvent e);
}
