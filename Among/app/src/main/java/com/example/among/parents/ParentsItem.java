package com.example.among.parents;

public class ParentsItem {
    String parents_user_id;
    String seq;
    String name;
    String phone_number;
    int img;

    public ParentsItem(){

    }
    //select
    public ParentsItem(String parents_user_id) {
        this.parents_user_id = parents_user_id;
    }

    public ParentsItem(String parents_user_id, String seq, String name, String phone_number, int img) {
        this.parents_user_id = parents_user_id;
        this.seq = seq;
        this.name = name;
        this.phone_number = phone_number;
        this.img = img;
    }
    //DB insert
    public ParentsItem(String parents_user_id, String name, String phone_number, int img) {
        this.parents_user_id = parents_user_id;
        this.name = name;
        this.phone_number = phone_number;
        this.img = img;
    }

    //child_insert
    public ParentsItem(String name, String phone_number, int img) {
        this.name = name;
        this.phone_number = phone_number;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getParents_user_id() {
        return parents_user_id;
    }

    public void setParents_user_id(String parents_user_id) {
        this.parents_user_id = parents_user_id;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }
}