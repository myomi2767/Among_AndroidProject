package com.example.among.chatting.model;

public class ExitMessage extends Message {
    //객체 생성되면 exit으로 메시지 생성
    public ExitMessage(){
        super.setMessageType(MessageType.EXIT);
    }
}
