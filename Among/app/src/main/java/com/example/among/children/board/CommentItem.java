package com.example.among.children.board;

public class CommentItem {
    int num;
    String comment;
    String date;
    String userid;
    String board_num;

    public CommentItem(){

    }
    //select
    public CommentItem(String board_num){
        this.board_num = board_num;
    }

    //delete
    public CommentItem(int num, String userid){
        this.num = num;
        this.userid = userid;
    }
    //insert
    public CommentItem(String comment, String date, String userid, String board_num) {
        this.comment = comment;
        this.date = date;
        this.userid = userid;
        this.board_num = board_num;
    }
    //update
    public CommentItem(int num, String comment, String userid) {
        this.num = num;
        this.comment = comment;
        this.userid = userid;
    }
    //basic
    public CommentItem(int num, String comment, String date, String userid, String board_num) {
        this.num = num;
        this.comment = comment;
        this.date = date;
        this.userid = userid;
        this.board_num = board_num;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBoard_num() {
        return board_num;
    }

    public void setBoard_num(String board_num) {
        this.board_num = board_num;
    }
}
