import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Client {
    // Using these for now
    private String hostName = "localhost";
    private final int portNumber = 1234;

    private PrintWriter output;
    private Socket socket;
    private BufferedReader in;
    private final String userName;
    private final ChatUI chatUI;
    private final Gson gson = new Gson();


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
        Message msg = new Message(Message.Type.TEXT, userName, userMessage);
        String json = gson.toJson(msg);
        output.println(json);
    }

    public void receiveMessage() {
        CompletableFuture.runAsync(() -> {
            try {
            String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    Message msg = gson.fromJson(serverMessage, Message.class);
                    switch (msg.getType()) {
                        case TEXT:
                            chatUI.addMessage(msg.getSender() + ": " + msg.getContent());
                            break;

                        case USER_LIST:
                            List<String> onlineUsers = gson.fromJson(msg.getContent(), new TypeToken<List<String>>(){}.getType());
                            chatUI.updateUserList(onlineUsers);

                            System.out.println(onlineUsers);
                            break;

                        case USER_JOINED:
                            chatUI.addUser(msg.getSender());
                            break;

                        case USER_LEFT:
                            chatUI.removeUser(msg.getSender());
                            break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
