package bean;

public class Message {

    private String name;
    private String message;
    private String username;
    private int type;

    public Message() {}

    public Message(int type,String name,String username, String message) {
        this.name = name;
        this.username = username;
        this.message = message;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Message{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
                ", username='" + username + '\'' +
                ", type=" + type +
                '}';
    }
}