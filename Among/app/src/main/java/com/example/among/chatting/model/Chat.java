package com.example.among.chatting.model;

import java.util.Date;

public class Chat {
    private String chatId;
    private String title;
    private Date createDate;
    private TextMessage lastMessage;
    private boolean disabled;
    private int totalUnreadCount;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public int getTotalUnreadCount() {
        return totalUnreadCount;
    }

    public void setTotalUnreadCount(int totalUnreadCount) {
        this.totalUnreadCount = totalUnreadCount;
    }

    public TextMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(TextMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "chatId='" + chatId + '\'' +
                ", title='" + title + '\'' +
                ", createDate=" + createDate +
                ", lastMessage=" + lastMessage +
                ", disabled=" + disabled +
                ", totalUnreadCount=" + totalUnreadCount +
                '}';
    }
}
