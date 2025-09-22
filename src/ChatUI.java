import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class ChatUI {
    private final Scene scene;
    private final TextArea chatArea;
    private final Client client;

    public ChatUI(String username) {
        client = new Client(username, this);

        BorderPane root = new BorderPane();

        chatArea = new TextArea();
        chatArea.setEditable(false);
        root.setCenter(chatArea);

        TextField inputField = new TextField();
        Button sendBtn = new Button("Send");

        sendBtn.setOnAction(e -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                chatArea.appendText(username + ": " + message + "\n");
                inputField.clear();
                client.sendMessage(message);
            }
        });

        BorderPane bottom = new BorderPane();
        bottom.setCenter(inputField);
        bottom.setRight(sendBtn);
        bottom.setPadding(new Insets(5));
        root.setBottom(bottom);

        this.scene = new Scene(root, 400, 300);
    }

    public void addMessage(String message) {
        chatArea.appendText(message + "\n");
    }

    public Scene getScene() {
        return scene;
    }

    public void exitChat() {
        client.disconnect();
    }
}
