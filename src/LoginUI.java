import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class LoginUI {

    private final Scene scene;

    public LoginUI(MessengerApp app) {

        Label userLabel = new Label("Username:");
        TextField userField = new TextField();
        userField.setPromptText("Enter username");

        Button loginBtn = new Button("Login");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(userLabel, 0, 0);
        grid.add(userField, 1, 0);
        grid.add(loginBtn, 1, 1);

        loginBtn.setOnAction(e -> {
            String username = userField.getText().trim();
            if (!username.isEmpty()) {
                app.showChat(username); // Switch to chat screen
            } else {
                new Alert(Alert.AlertType.ERROR, "Please enter a username!").showAndWait();
            }
        });

        this.scene = new Scene(grid, 300, 150);
    }

    public Scene getScene() {
        return scene;
    }
}
