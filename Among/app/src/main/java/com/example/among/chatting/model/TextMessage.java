package com.example.among.chatting.model;

public class TextMessage extends Message{
    private String messageText;

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    @Override
    public String toString() {
        return "TextMessage{" +
                "messageText='" + messageText + '\'' +
                '}';
    }
}
