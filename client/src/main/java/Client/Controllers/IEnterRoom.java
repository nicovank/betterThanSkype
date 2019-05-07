package Client.Controllers;

import Client.Events.RoomResponseEvent;
import javafx.scene.input.MouseEvent;

public interface IEnterRoom {

    void onCreateRoom(MouseEvent e);
    void onJoinRoom(MouseEvent e);
    void onNewRoomResponse(RoomResponseEvent e);
    void onJoinRoomResponse(RoomResponseEvent e);
}
