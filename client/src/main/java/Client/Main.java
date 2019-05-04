package Client;

import Client.Controllers.LoginController;
import Client.Controllers.MessagingController;
import Client.Events.EventNode;
import Client.Events.MessageReceivedEvent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.stage.Stage;
import java.util.HashMap;

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
        FXMLLoader messageLoader = new FXMLLoader(getClass().getResource("/fxml/messagingWindow.fxml"));
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Window<MessagingController> messageWindow = new Window<>(messageLoader.load(),messageLoader.getController(),"Better Than Skype",600,400);
        Window<LoginController> loginWindow = new Window<>(loginLoader.load(),loginLoader.getController(), "Login",300,400);
        scenes.put("Messaging Window",messageWindow);
        scenes.put("Login",loginWindow);
        activate("Login");

        eventNode = new EventNode();
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
}
