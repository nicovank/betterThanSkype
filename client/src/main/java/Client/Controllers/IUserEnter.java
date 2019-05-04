package Client.Controllers;

import Client.Events.UserJoinedEvent;

public interface IUserEnter {
    void userJoinedRoom(UserJoinedEvent e);
}
