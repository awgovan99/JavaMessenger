import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import com.google.gson.Gson;

public class ClientHandler implements Runnable {

    private final Server server;
    private final Socket socket;
    private final String userName;

    private final PrintWriter output;
    private final BufferedReader input;
    private final Gson gson = new Gson();


    public ClientHandler(Socket clientSocket, Server server) {
        try {

            this.server = server;
            socket = clientSocket;
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Get username from client
            userName = input.readLine();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void run() {
        receiveMessage();
    }

    public void receiveMessage() {
        Message joinMessage = new Message(Message.Type.USER_JOINED, userName, " has joined the chat!");
        server.broadcastMessage(gson.toJson(joinMessage), userName);

        String receivedMessage;
        try {
            while ((receivedMessage = input.readLine()) != null) {
                server.broadcastMessage(receivedMessage, userName);
            }
            disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void outPutMessage(String msg) {
        output.println(msg);
    }

    public String getUserName() {
        return userName;
    }

    public void disconnect() {
        try {
            Message exitMessage = new Message(Message.Type.USER_LEFT, userName, " has left the chat!");
            server.broadcastMessage(gson.toJson(exitMessage), userName);
            socket.close();
            server.removeClient(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
