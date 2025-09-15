import java.net.*;
import java.io.*;
import java.util.concurrent.*;


public class Client {
    // Using these for now
    private String hostName = "localhost";
    private final int portNumber = 1234;

    private PrintWriter output;
    private Socket socket;
    private BufferedReader in;
    private final String userName;
    private final ChatUI chatUI;


    public Client(String userName, ChatUI chatUI) {
        this.userName = userName;
        this.chatUI = chatUI;

        connect();
        receiveMessage();
    }

    public void connect() {
        try {

            socket = new Socket(hostName, portNumber);

            output = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send username to clientHandler
            output.println(userName);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String userMessage) {
        String msg = userName + ": " + userMessage;
        output.println(msg);
    }

    public void receiveMessage() {
        CompletableFuture.runAsync(() -> {
            try {
            String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    chatUI.addMessage(serverMessage);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
