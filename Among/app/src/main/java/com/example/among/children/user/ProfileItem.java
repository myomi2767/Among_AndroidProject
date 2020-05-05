package com.example.among.children.user;

public class ProfileItem {
    int profile_img;
    int profile_map;
    String profile_name;
    String profile_msg;

    public ProfileItem(int profile_img, String profile_name, String profile_msg, int profile_map) {
        this.profile_img = profile_img;
        this.profile_name = profile_name;
        this.profile_msg = profile_msg;
        this.profile_map = profile_map;
    }

    public int getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(int profile_img) {
        this.profile_img = profile_img;
    }

    public int getProfile_map() {
        return profile_map;
    }

    public void setProfile_map(int profile_map) {
        this.profile_map = profile_map;
    }

    public String getProfile_name() {
        return profile_name;
    }

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }

    public String getProfile_msg() {
        return profile_msg;
    }

    public void setProfile_msg(String profile_msg) {
        this.profile_msg = profile_msg;
    }

    @Override
    public String toString() {
        return "ProfileItem{" +
                "profile_img=" + profile_img +
                ", profile_map=" + profile_map +
                ", profile_name='" + profile_name + '\'' +
                ", profile_msg='" + profile_msg + '\'' +
                '}';
    }
}
