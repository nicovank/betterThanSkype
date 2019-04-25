import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;

public class Main extends Application {
    private HashMap<String, Parent> scenes;
    private Stage stage;
    private Scene main;
    private static Main instance;
    static Main getInstance(){
        return instance;
    }

    @Override
    public void start(Stage stage) throws Exception {
        instance = this;
        scenes = new HashMap<>();
        this.stage = stage;
        stage.setScene(main);
        scenes.put("Messaging Window",FXMLLoader.load(getClass().getResource("/fxml/messagingWindow.fxml")));
        scenes.put("Login",FXMLLoader.load(getClass().getResource("/fxml/login.fxml")));
        activate("Login","Better Than Skype - Login",300,400);

    }

    public static void main(String[] args) {
        launch(args);
    }

    void activate(String name, String title,int width, int height){
        main = new Scene(scenes.get(name),width,height);
        stage.setScene(main);
        stage.setTitle(title);
        stage.show();
    }
}
