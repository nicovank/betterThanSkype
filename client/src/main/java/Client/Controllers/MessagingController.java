package Client.Controllers;

import Client.Connectables.Message;
import Client.Sockets.RoomSocket;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    private RoomSocket roomSocket;

    @Override
    public boolean leaveRoom(String nickname, String roomID, String password) {
        return roomSocket.sendToEveryone("leaving",++timeStamp) && roomSocket.sendToServer("leaving"); //we are leaving
    }

    @Override
    public boolean sendMessage(String message) {
        return roomSocket.sendToEveryone(message,++timeStamp); //message
    }

    @Override
    public void receiveMessage() {
        Message s = roomSocket.receive();
        timeStamp = Math.max(s.getTimeStamp(), timeStamp);
        Message message = new Message(s.getText(),s.getUsername(),++timeStamp);
        messages.add(message);
        messages = messages.parallelStream().sorted().collect(Collectors.toList());
        chatBox.getChildren().clear();
        messages.forEach(m-> chatBox.getChildren().add(new Label(m.getFullText())));
    }

    void initializeChatRoom(String userName,String roomName,RoomSocket s){
        roomSocket = s;
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
        addMessageToChat(m);
        roomSocket.sendToEveryone(m.getText(),timeStamp);
        textBox.clear();
    }

    private void addMessageToChat(Message message) {
        chatBox.getChildren().add(new Label(message.getFullText()));
    }
}
