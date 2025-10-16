import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.image.Image;

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

    public void sendMessage(String userMessage, String recipient) {
        Message msg = new Message(Message.Type.TEXT, userName, userMessage, recipient);
        String json = gson.toJson(msg);
        output.println(json);
    }

    public void sendFile(File file, String recipient) {
        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            String encoded = Base64.getEncoder().encodeToString(fileBytes);
            String fileName = file.getName();
            String content = fileName + " : " + encoded;
            Message.Type messageType = Message.Type.FILE;

            if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".gif")) {
                messageType = Message.Type.IMAGE;
                Image img = new Image(new ByteArrayInputStream(fileBytes));
                chatUI.addImage(img);
            }

            Message msg = new Message(messageType, userName, content, recipient);

            String json = gson.toJson(msg);
            output.println(json);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveFile(Message msg) {
        try {
            // Create downloads folder if it doesn't exist
            File downloadDir = new File("Downloads");
            if (!downloadDir.exists()) {
                downloadDir.mkdirs();
            }

            String[] parts =  msg.getContent().split(" : ",2);
            String fileName = parts[0];
            String encoded = parts[1];

            // Place downloaded file into downloads folder
            File file = new File(downloadDir, fileName);
            byte[] fileBytes = Base64.getDecoder().decode(encoded);
            Files.write(file.toPath(), fileBytes).toFile();

            chatUI.addMessage("Received file " + fileName + " from " + msg.getSender());

            if(msg.getType() == Message.Type.IMAGE) {
                System.out.println("display image");
                Image img= new Image(new ByteArrayInputStream(fileBytes));
                chatUI.addImage(img);
            }

        } catch (IOException e) {
            chatUI.addMessage("Error saving file: " + e.getMessage());
        }
    }

    public void receiveMessage() {
        CompletableFuture.runAsync(() -> {
            try {
            String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    Message msg = gson.fromJson(serverMessage, Message.class);
                    switch (msg.getType()) {
                        case TEXT:
                            if(msg.getRecipient() == null) {
                                chatUI.addMessage("[Public] "+ msg.getSender() + ": " + msg.getContent());
                            } else{
                                chatUI.addMessage("[From " + msg.getSender() +"]: " + msg.getContent());
                            }
                            break;

                        case USER_LIST:
                            List<String> onlineUsers = gson.fromJson(msg.getContent(), new TypeToken<List<String>>(){}.getType());
                            chatUI.updateUserList(onlineUsers);
                            break;

                        case USER_JOINED:
                            chatUI.addUser(msg.getSender());
                            break;

                        case USER_LEFT:
                            chatUI.removeUser(msg.getSender());
                            break;

                        case FILE, IMAGE:
                            saveFile(msg);
                            break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
