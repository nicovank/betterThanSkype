package Client;

import Client.Controllers.LoginController;
import Client.Controllers.MessagingController;
import Client.Events.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.util.HashMap;

/**
 * this is the main javafx application, it handles creating the windows and handling custom events.
 * @author Jim Spagnola
 */
public class Main extends Application {
    private HashMap<String, Window> scenes;
    private Stage stage;
    private Window window;
    private static Main instance;
    private Node eventNode;

    public static Main getInstance(){
        return instance;
    }
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        instance = this;
        scenes = new HashMap<>();
        this.stage = stage;

        //load strings
        FXMLLoader messageLoader = new FXMLLoader(getClass().getResource("/fxml/messagingWindow.fxml"));
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Window<MessagingController> messageWindow = new Window<>(messageLoader.load(),messageLoader.getController(),"Better Than Skype",600,400);
        Window<LoginController> loginWindow = new Window<>(loginLoader.load(),loginLoader.getController(), "Login",300,400);
        scenes.put("Messaging Window",messageWindow);
        scenes.put("Login",loginWindow);
        activate("Login");

        //handle events
        eventNode = new EventNode();
        eventNode.addEventHandler(MessageReceivedEvent.MESSAGE_EVENT, messageReceivedEvent -> messageWindow.getController().receiveMessage(messageReceivedEvent));
        eventNode.addEventHandler(UserEvent.JOIN_EVENT, joinEvent -> messageWindow.getController().userJoinedRoom(joinEvent));
        eventNode.addEventHandler(UserEvent.LEAVE_EVENT, leftEvent -> messageWindow.getController().userLeftRoom(leftEvent));
        eventNode.addEventHandler(RoomResponseEvent.CREATE_ROOM, createEvent -> loginWindow.getController().onNewRoomResponse(createEvent));
        eventNode.addEventHandler(RoomResponseEvent.JOIN_ROOM, joinEvent -> loginWindow.getController().onJoinRoomResponse(joinEvent));
    }

    public Node getEventNode() {
        return eventNode;
    }

    public Window getCurrentWindow(){
        return window;
    }

    public void activate(String name){
        window = scenes.get(name);
        stage.setScene(window.getScene());
        stage.setTitle(window.getTitle());
        stage.show();
    }

    @Override
    public void stop() {
        MessagingController m = ((MessagingController)scenes.get("Messaging Window").getController());
        m.leaveRoom();
        System.exit(0);
    }
}
