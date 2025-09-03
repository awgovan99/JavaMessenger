import java.net.Socket;
import java.io.*;

public class ClientHandler implements Runnable {

    private final Server server;
    private final Socket socket;
    private final String userName;

    private final PrintWriter output;
    private final BufferedReader input;


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
        server.broadcastMessage(userName + " has joined the chat!", userName);

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
            server.broadcastMessage(userName + " has left the chat!", userName);
            socket.close();
            server.removeClient(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
