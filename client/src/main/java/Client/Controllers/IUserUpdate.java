package Client.Controllers;

import Client.Events.UserJoinedEvent;
import Client.Events.UserLeftEvent;

public interface IUserUpdate {
    void UserJoinedRoom(UserJoinedEvent e);
    void UserLeftRoom(UserLeftEvent e);
}
