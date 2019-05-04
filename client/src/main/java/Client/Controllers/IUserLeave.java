package Client.Controllers;

import Client.Events.UserLeftEvent;

public interface IUserLeave {
    void userLeftRoom(UserLeftEvent e);
}
