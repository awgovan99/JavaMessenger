import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

public class ChatUI {
    private final Scene scene;
//    private final TextArea chatArea;
    VBox chatBox;
    private final Client client;
    private final ObservableList<String> users = FXCollections.observableArrayList();
    private String selectedRecipient = null;


    public ChatUI(String username) {
        client = new Client(username, this);

        BorderPane root = new BorderPane();

        // ----- Chat Box -----a();

        chatBox = new VBox();
        ScrollPane scrollPane = new ScrollPane(chatBox);

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
        Button attachBtn = getAttachBtn();

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
                    addMessage("[To " + selectedRecipient + "] " + username + ": " + message);
                    // change back to public chat after each direct message
                    selectedRecipient = null;
                } else {
                    addMessage("[Public] " + username + ": " + message);
                }
            }
        });

        VBox chatAndInputBox = new VBox(10, scrollPane, inputBox);
        chatAndInputBox.setStyle("-fx-padding: 10;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        root.setCenter(chatAndInputBox);

        this.scene = new Scene(root, 500, 300);
    }

    private Button getAttachBtn() {
        Button attachBtn = new Button("📎");
        attachBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select File to Send");
            File file = fileChooser.showOpenDialog(this.getScene().getWindow());

            // Can only send file to 1 person at the moment
            if (file != null && selectedRecipient != null) {
                client.sendFile(file, selectedRecipient);
                addMessage("Sent file: " + file.getName());
                selectedRecipient = null;
            }
        });
        return attachBtn;
    }

    public void addMessage(String message) {
        Platform.runLater(() -> {
            Label messageLabel = new Label(message);
            chatBox.getChildren().add(messageLabel);
        });
    }

    public void addImage(Image img) {
        Platform.runLater(() -> {
            ImageView imageView = new ImageView(img);
            imageView.setFitWidth(200);
            imageView.setPreserveRatio(true);
            chatBox.getChildren().add(imageView);
        });
    }

    public void updateUserList(List<String> onlineUsers) {
        Platform.runLater(() -> {
            users.setAll(onlineUsers);
        });
    }

    public void addUser(String username) {
        Platform.runLater(() -> {
            users.add(username);
            addMessage(username + " has joined the chat!");
        });
    }

    public void removeUser(String username) {
        Platform.runLater(() -> {
            users.remove(username);
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
