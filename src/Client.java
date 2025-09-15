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


    public Client(String userName) {
        this.userName = userName;

        connect();
        receiveMessage();
        sendMessage();
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

    // Don't need to thread this for now
    public void sendMessage() {
        String userMessage;
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Input a message: ");

            while ((userMessage = stdin.readLine()) != null) {
                if(userMessage.equals("exit")) break;

                String msg = userName + ": " + userMessage;
                output.println(msg);
                System.out.println(msg);

                System.out.println("Input a message: ");
            }

            disconnect();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            disconnect();
        }
    }

    public void receiveMessage() {
        CompletableFuture.runAsync(() -> {
            try {
            String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println(serverMessage);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
