import java.net.*;
import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Message comes in from client socket
// Then need to repeat message to all other clients
// ClientHandler will handle input for each client socket
// Server will then output message to all clients except the one the message is from

public class Server {

    final int portNumber = 1234;

    private final ServerSocket serverSocket;
    private final List<ClientHandler> clients;
    private final ExecutorService executor;

    public static void main(String[] args) {
        Server server = new Server();
        server.connectClients();

    }

    public Server() {
        try {

            serverSocket = new ServerSocket(portNumber);
            clients = new CopyOnWriteArrayList<>(); // Makes list thread safe
            executor = Executors.newVirtualThreadPerTaskExecutor();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void connectClients() {
        try {
            while(true) {
                Socket client = serverSocket.accept();
                ClientHandler handler = new ClientHandler(client, this);
                clients.add(handler);
                executor.submit(handler);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void broadcastMessage(String message, String sender) {
        for (ClientHandler client : clients) {
            if(!Objects.equals(client.getUserName(), sender)) {
                client.outPutMessage(message);
            }
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

}
