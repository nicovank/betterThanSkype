public interface IEnterRoom {

    boolean requestNewRoom(String nickname, String roomID, String password);
    boolean requestToJoinRoom(String nickname, String roomID, String password);
    boolean receiveRoomData();
}
