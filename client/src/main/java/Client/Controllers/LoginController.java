package Client.Controllers;

import Client.IEnterRoom;
import Client.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;


public class LoginController implements IEnterRoom {
    @FXML
    private TextField nicknameField;
    @FXML
    private TextField roomNameLabel;
    @FXML
    private PasswordField passwordField;

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
    private void onCreateRoom(MouseEvent e){
        System.out.println("Create Room");
        System.out.println("Primary Click");
        if(hasCorrectInput()){
            System.out.println("Moving Scene");
            Main.getInstance().activate("Messaging Client.Window");
            MessagingController messagingController = (MessagingController)Main.getInstance().getCurrentWindow().getController();
            messagingController.initializeChatRoom(nicknameField.getText(),roomNameLabel.getText());
        }
    }

    private boolean hasCorrectInput(){
        try{
            if(nicknameField.getText().isBlank() || nicknameField.getText().isEmpty())
                throw new EmptyFieldException(nicknameField);
            else if(roomNameLabel.getText().isBlank() || roomNameLabel.getText().isEmpty())
                throw new EmptyFieldException(roomNameLabel);
        } catch (EmptyFieldException ex) {

            ex.getEmptyField().requestFocus();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Empty Field Found!");
            errorAlert.setContentText("Your nickname and room name must be filled");
            errorAlert.showAndWait();
            return false;
        }
        return true;
    }
}
