package Client;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class Window<T> {
    private Scene scene;
    private T controller;
    private String title;

    Window(Parent parent, T controller, String title, int width, int height){
        scene = new Scene(parent,width,height);
        this.controller = controller;
        this.title = title;
    }

    Scene getScene() {
        return scene;
    }

    public T getController() {
        return controller;
    }

    String getTitle() {
        return title;
    }

}
