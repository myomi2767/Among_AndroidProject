package com.example.among.children.board;

public class CommunityListViewItem {
    int seq;
    String title;
    String text;
    String date;
    String userid;

    public CommunityListViewItem(){

    }

    public CommunityListViewItem(int seq, String userid) {
        this.seq = seq;
        this.userid = userid;
    }

    public CommunityListViewItem(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public CommunityListViewItem(String title, String text, String userid) {
        this.title = title;
        this.text = text;
        this.userid = userid;
    }

    public CommunityListViewItem(int seq, String title, String text) {
        this.seq = seq;
        this.title = title;
        this.text = text;
    }
    public CommunityListViewItem(int seq, String title, String text, String userid) {
        this.seq = seq;
        this.title = title;
        this.text = text;
        this.userid = userid;
    }

    public CommunityListViewItem(String title, String text, String date, String userid) {
        this.title = title;
        this.text = text;
        this.date = date;
        this.userid = userid;
    }

    public CommunityListViewItem(int seq, String title, String text, String date, String userid) {
        this.seq = seq;
        this.title = title;
        this.text = text;
        this.date = date;
        this.userid = userid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
