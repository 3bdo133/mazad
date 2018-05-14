package mazad.mazad.models;

import java.io.Serializable;

public class ChatModel implements Serializable {

    String chatId;
    String lastMessage;
    String seen;
    String name;
    String toId;
    String email;
    String messageSenderId;


    public ChatModel(String chatId, String lastMessage, String seen, String name, String toId, String email, String messageSenderId) {
        this.chatId = chatId;
        this.lastMessage = lastMessage;
        this.seen = seen;
        this.name = name;
        this.toId = toId;
        this.email = email;
        this.messageSenderId = messageSenderId;
    }


    public String getChatId() {
        return chatId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getSeen() {
        return seen;
    }

    public String getName() {
        return name;
    }

    public String getToId() {
        return toId;
    }

    public String getEmail() {
        return email;
    }

    public String getMessageSenderId() {
        return messageSenderId;
    }
}
