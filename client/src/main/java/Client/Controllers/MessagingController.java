package Client.Controllers;

import Client.Connectables.Message;
import Client.ILeaveRoom;
import Client.IMessage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

public class MessagingController implements IMessage, ILeaveRoom {
    private String userName;
    @FXML
    private TextArea textBox;
    @FXML
    private VBox chatBox;
    @FXML
    private Label roomName;
    @FXML
    private VBox memberList;
    private long timeStamp = 0;
    private List<Message> messages = new ArrayList<>();

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

    void initializeChatRoom(String userName,String roomName){
        timeStamp = 0;
        this.userName = userName;
        this.roomName.setText(roomName);
        memberList.getChildren().add(new Label(userName)); //when video/audio gets added this will need changing
    }

    @FXML
    public void onEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER) {
            addLocalMessage();
        }
    }

    @FXML
    public void onSubmit(MouseEvent e){
        addLocalMessage();
    }

    private void addLocalMessage(){
        String message = textBox.getText().replace("\n","");
        Message m = new Message(message, userName, ++timeStamp);
        messages.add(m);
        addMessageToChat(userName, m);
        textBox.clear();
    }

    private void addMessageToChat(String userName, Message message) {
        chatBox.getChildren().add(new Label(userName+" says: \"" +message.getText()+"\""));
    }
}
