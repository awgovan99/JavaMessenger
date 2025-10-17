import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;
import java.util.Objects;

public class ChatUI {
    private final Scene scene;
    private final VBox chatBox;
    private final Client client;
    private final ObservableList<String> users = FXCollections.observableArrayList();
    private String selectedRecipient = null;
    private final String userName;


    public ChatUI(String username) {
        client = new Client(username, this);
        this.userName = username;

        BorderPane root = new BorderPane();

        // ----- Chat Box -----;

        chatBox = new VBox(5);
        ScrollPane scrollPane = new ScrollPane(chatBox);
        scrollPane.setStyle("-fx-padding: 5;");

        // ----- User list sidebar -----
        Label userListLabel = new Label("Online Users");
        userListLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        ListView<String> userListView = new ListView<>(users);
        userListView.setPrefWidth(150);
        userListView.setPlaceholder(new Label("No users online"));
        VBox userListBox = new VBox(5, userListLabel, userListView);
        userListBox.setPrefWidth(200);
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
                addMessage(userName, message, selectedRecipient);
            }
        });

        VBox chatAndInputBox = new VBox(10, scrollPane, inputBox);
        chatAndInputBox.setStyle("-fx-padding: 10;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        root.setCenter(chatAndInputBox);

        this.scene = new Scene(root, 800, 500);
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
                String fileMsg = "Sent file " + file.getName();
                addMessage("Server", fileMsg, selectedRecipient);
                selectedRecipient = null;
            }
        });
        return attachBtn;
    }

    public void addMessage(String sender, String content, String recipient) {
        Platform.runLater(() -> {

            TextFlow messageFlow = getMessageFlow(sender, content, recipient);
            messageFlow.setMaxWidth(400);
            messageFlow.setPadding(new Insets(5, 10, 5, 10));
            messageFlow.setStyle("-fx-background-color: #d9dcde; -fx-background-radius: 10;"); // Make gray slightly darker

            HBox wrapper = new HBox(messageFlow);
            wrapper.setAlignment(Pos.CENTER_LEFT);
            chatBox.getChildren().add(wrapper);

        });
    }

    private TextFlow getMessageFlow(String sender, String content, String recipient) {
        Text nameText;
        boolean isOwnMessage = Objects.equals(sender, userName);

        if(recipient == null) {
            nameText = new Text("[Public] "+ sender + ": ");
        } else if (isOwnMessage){
            nameText = new Text("[To " + recipient +"]: ");
        }
        else{
            nameText = new Text("[From " + sender +"]: ");
        }

        nameText.setStyle("-fx-font-weight: bold; -fx-fill: #2c3e50;");
        Text messageText = new Text(content);
        messageText.setStyle("-fx-fill: #333333;");

        return new TextFlow(nameText, messageText);
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

    public void addUser(String user) {
        Platform.runLater(() -> {
            users.add(user);
            addMessage("Server", user + " has joined the chat!", null);
        });
    }

    public void removeUser(String user) {
        Platform.runLater(() -> {
            users.remove(user);
            addMessage("Server", user + " has left the chat!", null);
        });
    }

    public Scene getScene() {
        return scene;
    }

    public void exitChat() {
        client.disconnect();
    }
}
