import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.List;

public class ChatUI {
    private final Scene scene;
    private final TextArea chatArea;
    private final Client client;
    private final ObservableList<String> users = FXCollections.observableArrayList();


    public ChatUI(String username) {
        client = new Client(username, this);

        BorderPane root = new BorderPane();

        // ----- Chat area -----
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);

        // ----- User list sidebar -----
        Label userListLabel = new Label("Online Users");
        userListLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        ListView<String> userListView = new ListView<>(users);
        userListView.setPrefWidth(100);
        userListView.setPlaceholder(new Label("No users online"));
        VBox userListBox = new VBox(5, userListLabel, userListView);
        userListBox.setPrefWidth(150);
        userListBox.setStyle("-fx-background-color: #f2f2f2; -fx-padding: 10;");

        root.setRight(userListBox);

        // ----- Input area -----
        TextField inputField = new TextField();
        Button sendBtn = new Button("Send");
        HBox inputBox = new HBox(10, inputField, sendBtn);
        HBox.setHgrow(inputField, Priority.ALWAYS);

        sendBtn.setOnAction(e -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                chatArea.appendText("[Public] "+ username + ": " + message + "\n");
                inputField.clear();
                client.sendMessage(message);
            }
        });

        VBox chatAndInputBox = new VBox(10, chatArea, inputBox);
        chatAndInputBox.setStyle("-fx-padding: 10;");
        VBox.setVgrow(chatArea, Priority.ALWAYS);
        root.setCenter(chatAndInputBox);

        this.scene = new Scene(root, 500, 300);
    }

    public void addMessage(String message) {
        chatArea.appendText(message + "\n");
    }

    public void updateUserList(List<String> onlineUsers) {
        Platform.runLater(() -> {
            users.setAll(onlineUsers);
        });
    }

    public void addUser(String username) {
        Platform.runLater(() -> {
            users.add(username);

            System.out.println(users);

            addMessage(username + " has joined the chat!");
        });
    }

    public void removeUser(String username) {
        Platform.runLater(() -> {
            users.remove(username);

            System.out.println(users);

            addMessage(username + " has left the chat!");
        });
    }

    public Scene getScene() {
        return scene;
    }

    public void exitChat() {
        client.disconnect();
    }
}
