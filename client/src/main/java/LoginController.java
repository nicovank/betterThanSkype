public class LoginController implements IEnterRoom {

    @Override
    public boolean requestNewRoom(String nickname, String roomID, String password) {
        return false;
    }

    @Override
    public boolean requestToJoinRoom(String nickname, String roomID, String password) {
        return false;
    }

    @Override
    public boolean receiveRoomData() {
        return false;
    }
}
