import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;


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

    @FXML
    private void onJoin(MouseEvent e){
        if(e.isPrimaryButtonDown())
            Main.getInstance().activate("Messaging Window", "Better Than Skype", 800,400 );
    }
}
