package Client.Controllers;

/**
 * This interface requires methods that respond and send information pertaining to leaving
 * a room.
 * @author Jim Spagnola
 */
public interface ILeaveRoom {

    void leaveRoom(String nickname,String roomID, String password);
}
