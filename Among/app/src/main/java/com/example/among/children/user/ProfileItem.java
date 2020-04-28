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
