public class Message {

    public enum Type {
        TEXT,
        USER_JOINED,
        USER_LEFT
    }

    private final Type type;
    private final String sender;
    private final String content;

    public Message(Type type, String sender, String content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
    }

    public Type getType() {return type;}

    public String getSender() {return sender;}

    public String getContent() {return content;}

}
