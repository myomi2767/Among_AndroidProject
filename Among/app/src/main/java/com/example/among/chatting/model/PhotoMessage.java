package com.example.among.chatting.model;

public class PhotoMessage extends Message {
    private String PhotoUrl;

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "PhotoMessage{" +
                "PhotoUrl='" + PhotoUrl + '\'' +
                '}';
    }
}
