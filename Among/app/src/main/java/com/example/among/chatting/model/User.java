package com.example.among.chatting.model;

public class User {
    private String uid;
    private String email;
    private String name;
    private String profileUrl;
    private boolean selection;

    public User(){

    }
    public User(String uid,String email,String name,String profileUrl){
        this.uid=uid;
        this.email=email;
        this.name=name;
        this.profileUrl=profileUrl;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelection() {
        return selection;
    }

    public void setSelection(boolean selection) {
        this.selection = selection;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", profileUrl='" + profileUrl + '\'' +
                '}';
    }
}
