import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class ChatUI {
    private final Scene scene;
    private final TextArea chatArea;
    private final Client client;
    private final ObservableList<String> users = FXCollections.observableArrayList();
    private String selectedRecipient = null;


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

        // Handle user click for DM
        userListView.setOnMouseClicked(e -> {
            selectedRecipient = userListView.getSelectionModel().getSelectedItem();
        });

        // ----- File sending -----
        Button attachBtn = new Button("📎");
        attachBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select File to Send");
            File file = fileChooser.showOpenDialog(this.getScene().getWindow());

            // Can only send file to 1 person at the moment
            if (file != null && selectedRecipient != null) {
                client.sendFile(file, selectedRecipient);
                chatArea.appendText("Sent file: " + file.getName() + "\n");
            }
        });

        // ----- Input area -----
        TextField inputField = new TextField();
        Button sendBtn = new Button("Send");
        HBox inputBox = new HBox(10, inputField, sendBtn, attachBtn);
        HBox.setHgrow(inputField, Priority.ALWAYS);

        // Handle sending message
        sendBtn.setOnAction(e -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                inputField.clear();
                client.sendMessage(message, selectedRecipient);

                if (selectedRecipient != null) {
                    chatArea.appendText("[To " + selectedRecipient + "] " + username + ": " + message + "\n");

                    // change back to public chat after each direct message
                    selectedRecipient = null;
                } else {
                    chatArea.appendText("[Public] " + username + ": " + message + "\n");
                }
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
