package Client.Controllers;

import Client.Events.Message;
import Client.Events.MessageReceivedEvent;
import Client.Events.UserEvent;
import Client.Sockets.IRoomSocket;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class controls messaging between clients and the server while in a room.
 * It sends events through a RoomSocket and receives events through Javafx
 * @author Jim Spagnola
 */
public class MessagingController implements IMessage, ILeaveRoom, IUserChange {
    private String username;
    @FXML
    private TextArea textBox;
    @FXML
    private VBox chatBox;
    @FXML
    private Label roomName;
    @FXML
    private VBox memberList;
    @FXML
    private ScrollPane scrollPane;

    private List<Message> messages = new ArrayList<>();
    private IRoomSocket roomSocket;

    private String password;

    /**
     * This event occurs when someone leaves the room.
     * @param nickname the specific user
     * @param roomID the specific room
     * @param password authentication password
     * @author Jim Spagnola
     */
    @Override
    public void leaveRoom(String nickname, String roomID, String password) {
        roomSocket.sendLeavingMessage(nickname,roomID);
    }

    /**
     * this method is called whenever we want to send a message to everyone.
     * @param message
     */
    @Override
    public void sendMessage(String message) {
        roomSocket.sendToEveryone(username,message,password); //message
    }

    /**
     * this event occurs whenever we receive a message from the socket.
     * @param event this event contains info on who sent the message and when.
     */
    @Override
    public void receiveMessage(MessageReceivedEvent event) {
        Message message = event.getMessage();
        messages.add(message);
        messages = messages.parallelStream().sorted().collect(Collectors.toList());
        chatBox.getChildren().clear();
        messages.forEach(m-> chatBox.getChildren().add(new Label(m.getFullText())));
    }

    /**
     * this takes place after we successfully join or create a room.  it is similar to a constructor
     * @param username our user name for this session
     * @param roomName the name of the room we joined or created
     * @param password the password to enter the room
     * @param s the room socket that is maintaining the connection
     * @author Jim Spagnola
     */
    void initializeChatRoom(String username, String roomName, String password, IRoomSocket s){
        roomSocket = s;
        this.username = username;
        this.roomName.setText(roomName);
        this.password = password;
        memberList.getChildren().add(new Label(username)); //when video/audio gets added this will need changing
    }

    /**
     * This method is called whenever the presses enter.
     * it will send a message to all users
     * @param e
     * @author Jim Spagnola
     */
    @FXML
    public void onEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER) {
            addLocalMessage();
        }
    }

    /**
     * this method is called whenever the user selects the "submit" button.
     * it will send a message to all users
     * @param e
     * @author Jim Spagnola
     */
    @FXML
    public void onSubmit(MouseEvent e){
        addLocalMessage();
    }

    /**
     * this method handles a message being sent from the user.
     * @author Jim Spagnola
     */
    private void addLocalMessage(){
        String message = textBox.getText().replace("\n","");
        if(!message.isEmpty() && !message.isBlank()) {
            long timeStamp = roomSocket.sendToEveryone(username,message, password);
            Message m = new Message(message, username, timeStamp);
            messages.add(m);
            addMessageToChat(m);
            textBox.clear();
        }
    }

    /**
     * place the message on the gui
     * @param message the message
     */
    private void addMessageToChat(Message message) {
        chatBox.getChildren().add(new Label(message.getFullText()));
        scrollPane.vvalueProperty().bind(chatBox.heightProperty());
    }

    /**
     * this method is called when a user joins the room.
     * @param e the event containing user information
     */
    @Override
    public void userJoinedRoom(UserEvent e) {
        if(memberList.getChildren().stream().noneMatch(l->{
            if(l instanceof Label){
                return ((Label) l).getText().equals(e.getUsername());
            }
            else
                return false;
        }))
            memberList.getChildren().add(new Label(e.getUsername()));
    }

    /**
     * this method is called when a user leaves the room.
     * @param e the event containing user information
     */
    @Override
    public void userLeftRoom(UserEvent e) {
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
