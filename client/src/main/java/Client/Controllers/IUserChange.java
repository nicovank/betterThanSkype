package Client.Controllers;

import Client.Events.UserEvent;

/**
 * This interface requires methods that handle user status changes
 * @author Jim Spagnola
 */
public interface IUserChange {
    void userJoinedRoom(UserEvent e);
    void userLeftRoom(UserEvent e);
}
