import com.google.gson.Gson;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {

    final int portNumber = 1234;

    private final ServerSocket serverSocket;
    private final ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private final ExecutorService executor;
    private final Gson gson = new Gson();

    public static void main(String[] args) {
        Server server = new Server();
        server.connectClients();

    }

    public Server() {
        try {

            serverSocket = new ServerSocket(portNumber);
            executor = Executors.newVirtualThreadPerTaskExecutor();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // might need to thread this
    public void connectClients() {
        try {
            while(true) {
                Socket client = serverSocket.accept();
                ClientHandler handler = new ClientHandler(client, this);
                executor.submit(handler);

                // Send list of all online users to connected client
                sendUserList(handler);

                clients.put(handler.getUserName(), handler);

                System.out.println(clients);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void broadcastMessage(Message msg) {
        String sender = msg.getSender();
        String recipient = msg.getRecipient();
        String msgJson = gson.toJson(msg);

        if(recipient == null) {
            // Might change loop condition
            for (ClientHandler client : clients.values()) {
                if (!Objects.equals(client.getUserName(), sender)) {
                    client.outPutMessage(msgJson);
                }
            }
        } else{
            clients.get(recipient).outPutMessage(msgJson);
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client.getUserName());

        System.out.println(clients);
    }

    public void sendUserList(ClientHandler client) {
        List<String> usernames = new ArrayList<>(clients.keySet());
        Message usersListMsg = new Message(Message.Type.USER_LIST, "SERVER", gson.toJson(usernames), null);

        client.outPutMessage(gson.toJson(usersListMsg));
    }
}
