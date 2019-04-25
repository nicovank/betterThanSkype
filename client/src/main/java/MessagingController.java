public class MessagingController implements IMessage,ILeaveRoom {
    @Override
    public boolean leaveRoom(String nickname, String roomID, String password) {
        return false;
    }

    @Override
    public boolean sendMessage(String message) {
        return false;
    }

    @Override
    public String receiveMessage() {
        return null;
    }
}
