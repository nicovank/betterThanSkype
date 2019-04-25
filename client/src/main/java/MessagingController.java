import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;


public class MessagingController implements IMessage,ILeaveRoom {
    private String userName = "Tim Tester";
    @FXML
    private TextArea textBox;
    @FXML
    private VBox chatBox;

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

    @FXML
    public void onEnter(KeyEvent e){

        if(e.getCode() == KeyCode.ENTER) {
            addMessageToChat(userName,textBox.getText());
            textBox.clear();
        }
    }

    @FXML
    public void onSubmit(MouseEvent e){
        if(e.isPrimaryButtonDown()) {
            addMessageToChat(userName, textBox.getText());
            textBox.clear();
        }
    }

    private void addMessageToChat(String userName,String message) {
        chatBox.getChildren().add(new Label(userName+" says: \"" +message+"\""));

    }
}
