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

/**
 * This controller handles the login procedures, information and events required to enter
 * a chat room.
 * @author Jim Spagnola
 */
public class LoginController implements IEnterRoom {
    @FXML
    private TextField nicknameField;
    @FXML
    private TextField roomNameLabel;
    @FXML
    private PasswordField passwordField;

    private IRoomSocket roomSocket;

    /**
     * its a constructor.  if an exception is raised, the program should terminate.
     * It uses a socket to communicate events needed across the network.
     * @throws IOException thrown if the socket fails to connect
     * @throws CryptoException thrown if the socket connection fails establishing encryption
     * @author Jim Spagnola
     */
    public LoginController() throws IOException, CryptoException {
        roomSocket = new RoomSocket();
        new Thread((Runnable) roomSocket).start();
    }

    /**
     * When a response from the authentication server is received pertaining to
     * creating a new room this method is called.
     * If the response accepts the connection we move on to the next scene.
     * else we handle the error by notifying the user of the rejection
     * @param event a custom event that triggers the response
     * @author Jim Spagnola
     */
    @Override
    public void onNewRoomResponse(RoomResponseEvent event) {
        if(event.getResponse()){
            moveScene();
        } else{
            //TODO handle error informtion
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Empty Field Found!");
            errorAlert.setContentText("The server was unable to create a new room");
            errorAlert.showAndWait();
        }
    }

    /**
     * When a response from the authentication server is received pertaining to
     * joining a room this method is called.
     * If the response accepts the connection we move on to the next scene.
     * else we handle the error by notifying the user of the rejection
     * @param event a custom event that triggers the response
     * @author Jim Spagnola
     */
    @Override
    public void onJoinRoomResponse(RoomResponseEvent event) {
        if(event.getResponse()){
            moveScene();
            roomSocket.announce(nicknameField.getText(),passwordField.getText());
        } else{
            //TODO handle error informtion
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Empty Field Found!");
            errorAlert.setContentText("The server was unable to connect you to a room");
            errorAlert.showAndWait();
        }
    }

    /**
     * This event is called when the user indicates they want to create a new room.
     * The event takes information from the GUI, checks for any errors.
     * If there are none, then a request to the server is made to create a new room.
     * @param event
     */
    @Override
    @FXML
    public void onCreateRoom(MouseEvent event){
        if(hasCorrectInput()){
            //create room
            roomSocket.attemptToCreateRoom(roomNameLabel.getText(),nicknameField.getText(),passwordField.getText());
        }
    }

    /**
     * This event is called when the user indicates they want to create a new room.
     * The event takes information from the GUI, checks for any errors.
     * If there are none, then a request to the server is made to join a room.
     * @param event
     */
    @Override
    @FXML
    public void onJoinRoom(MouseEvent event){
        if(hasCorrectInput()){
            //and authentication for room
            roomSocket.attemptToJoinRoom(nicknameField.getText(), roomNameLabel.getText(),passwordField.getText());
        }
    }

    /**
     * This method transitions from the login scene to the room scene.
     * @author Jim Spagnola
     */
    private void moveScene(){
        System.out.println("Moving Scene");
        Main.getInstance().activate("Messaging Window");
        MessagingController messagingController = (MessagingController)Main.getInstance().getCurrentWindow().getController();
        messagingController.initializeChatRoom(nicknameField.getText(),roomNameLabel.getText(),passwordField.getText(),roomSocket);
    }

    /**
     * this method checks the name, room, and password fields to make sure they are not empty.
     * If they all have data in their respective fields, it will return true.  If a field
     * does not, it will raise an exception that will focus the respective field.
     * @return all fields have information.
     */
    private boolean hasCorrectInput(){
        try{
            //if a field is empty throw an exception
            if(nicknameField.getText().isBlank() || nicknameField.getText().isEmpty())
                throw new EmptyFieldException(nicknameField);
            else if(roomNameLabel.getText().isBlank() || roomNameLabel.getText().isEmpty())
                throw new EmptyFieldException(roomNameLabel);
            else if(passwordField.getText().isBlank() || passwordField.getText().isEmpty())
                throw new EmptyFieldException(passwordField);
        } catch (EmptyFieldException ex) {
            //alert the user and focus it.
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
