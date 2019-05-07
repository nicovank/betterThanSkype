package Client.Controllers;

import Client.Events.RoomResponseEvent;
import Client.Main;
import Client.Sockets.IRoomSocket;
import Client.Sockets.RoomSocket;
import crypto.CryptoException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class LoginController implements IEnterRoom {
    @FXML
    private TextField nicknameField;
    @FXML
    private TextField roomNameLabel;
    @FXML
    private PasswordField passwordField;

    private IRoomSocket roomSocket;

    public LoginController() throws IOException, CryptoException {
        roomSocket = new RoomSocket();
    }
    //TODO finish implementation of the events
    @Override
    public void onNewRoomResponse(RoomResponseEvent e) {
        if(e.getResponse()){
            //TODO more join stuff?
            moveScene();
        } else{
            //TODO handle error informtion
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Empty Field Found!");
            errorAlert.setContentText("The server was unable to create a new room");
            errorAlert.showAndWait();
        }
    }

    @Override
    public void onJoinRoomResponse(RoomResponseEvent e) {
        if(e.getResponse()){
            //TODO more join stuff?
            moveScene();
        } else{
            //TODO handle error informtion
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Empty Field Found!");
            errorAlert.setContentText("The server was unable to connect you to a room");
            errorAlert.showAndWait();
        }
    }


    @Override
    @FXML
    public void onCreateRoom(MouseEvent e){
        if(hasCorrectInput()){
            //create room
            roomSocket.attemptToCreateRoom(roomNameLabel.getText(),nicknameField.getText(),passwordField.getText());
        }
    }

    @Override
    @FXML
    public void onJoinRoom(MouseEvent e){
        if(hasCorrectInput()){
            //and authentication for room
            roomSocket.attemptToJoinRoom(roomNameLabel.getText(),nicknameField.getText(),passwordField.getText());
        }
    }

    private void moveScene(){
        System.out.println("Moving Scene");
        Main.getInstance().activate("Messaging Window");
        MessagingController messagingController = (MessagingController)Main.getInstance().getCurrentWindow().getController();
        messagingController.initializeChatRoom(nicknameField.getText(),roomNameLabel.getText(),passwordField.getText(),roomSocket);
    }

    private boolean hasCorrectInput(){
        try{
            if(nicknameField.getText().isBlank() || nicknameField.getText().isEmpty())
                throw new EmptyFieldException(nicknameField);
            else if(roomNameLabel.getText().isBlank() || roomNameLabel.getText().isEmpty())
                throw new EmptyFieldException(roomNameLabel);
            else if(passwordField.getText().isBlank() || passwordField.getText().isEmpty())
                throw new EmptyFieldException(passwordField);
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
