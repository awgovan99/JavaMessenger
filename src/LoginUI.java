import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;

public class LoginUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Label userLabel = new Label("Username:");
        TextField userField = new TextField();
        userField.setPromptText("Enter username");

        Button loginBtn = new Button("Login");

        // Layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(userLabel, 0, 0);
        grid.add(userField, 1, 0);
        grid.add(loginBtn, 1, 1);

        Scene loginScene = new Scene(grid, 300, 150);
        stage.setScene(loginScene);
        stage.setTitle("Login");
        stage.show();

        loginBtn.setOnAction(e -> {
            String username = userField.getText();
            // Could check if empty

            if (!username.isEmpty()) {
                stage.close();
                // Open clientGUI, just use client for now
                new Client(username);
            } else {
                new Alert(Alert.AlertType.ERROR, "Please enter a username!").showAndWait();
            }
        });
    }
}
