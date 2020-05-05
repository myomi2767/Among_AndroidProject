package com.example.among.children.map.familyChat;

import android.app.Application;

import com.example.among.children.user.User;


public class UserClient extends Application {

    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}