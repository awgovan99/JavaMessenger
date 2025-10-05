public class Message {

    public enum Type {
        TEXT,
        USER_JOINED,
        USER_LEFT,
        USER_LIST
    }

    private final Type type;
    private final String sender;
    private final String content;
    private final String recipient;

    public Message(Type type, String sender, String content, String recipient) {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.recipient = recipient;
    }

    public Type getType() {return type;}

    public String getSender() {return sender;}

    public String getContent() {return content;}

    public String getRecipient() {return recipient;}

}
