import javafx.application.Application;
import javafx.stage.Stage;

public class MessengerApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        showLogin();
    }

    public void showLogin() {
        LoginUI loginUI = new LoginUI(this);
        primaryStage.setScene(loginUI.getScene());
        primaryStage.setTitle("Messenger Login");
        primaryStage.show();
    }

    public void showChat(String username) {
        ChatUI chatUI = new ChatUI(username);
        primaryStage.setScene(chatUI.getScene());
        primaryStage.setTitle("Messenger - " + username);

        primaryStage.setOnCloseRequest(event -> {
            // Close client socket when window closed
            chatUI.exitChat();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
