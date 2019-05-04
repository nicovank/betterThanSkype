package Client.Controllers;

import Client.Events.Message;
import Client.Events.MessageReceivedEvent;
import Client.Events.UserJoinedEvent;
import Client.Events.UserLeftEvent;
import Client.Sockets.IRoomSocket;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MessagingController implements IMessage, ILeaveRoom, IUserEnter,IUserLeave {
    private String username;
    @FXML
    private TextArea textBox;
    @FXML
    private VBox chatBox;
    @FXML
    private Label roomName;
    @FXML
    private VBox memberList;
    private List<Message> messages = new ArrayList<>();
    private IRoomSocket roomSocket;

    private String password;

    @Override
    public void leaveRoom(String nickname, String roomID, String password) {
        roomSocket.sendLeavingMessage(nickname);
    }

    @Override
    public void sendMessage(String message) {
        roomSocket.sendToEveryone(message,password); //message
    }

    @Override
    public void receiveMessage(MessageReceivedEvent event) {
        Message message = event.getMessage();
        messages.add(message);
        messages = messages.parallelStream().sorted().collect(Collectors.toList());
        chatBox.getChildren().clear();
        messages.forEach(m-> chatBox.getChildren().add(new Label(m.getFullText())));
    }

    void initializeChatRoom(String username, String roomName, String password, IRoomSocket s){
        roomSocket = s;
        this.username = username;
        this.roomName.setText(roomName);
        this.password = password;
        memberList.getChildren().add(new Label(username)); //when video/audio gets added this will need changing
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
        long timeStamp = roomSocket.sendToEveryone(message,password);
        Message m = new Message(message, username,timeStamp);
        messages.add(m);
        addMessageToChat(m);
        textBox.clear();
    }

    private void addMessageToChat(Message message) {
        chatBox.getChildren().add(new Label(message.getFullText()));
    }

    @Override
    public void userJoinedRoom(UserJoinedEvent e) {
        memberList.getChildren().add(new Label(e.getUsername()));
    }

    @Override
    public void userLeftRoom(UserLeftEvent e) {
        memberList.getChildren().removeIf(t->{
            if( t instanceof Label){
                Label tt = (Label)t;
                String text = tt.getText();
                return text.equals(e.getUsername());
            }
            return false;
        });
    }
}
