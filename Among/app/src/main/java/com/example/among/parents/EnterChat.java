package com.example.among.parents;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.example.among.R;
import com.example.among.chatting.ChattingFragment;
import com.example.among.chatting.FriendFragment;

public class EnterChat extends AppCompatActivity {
    FriendFragment friendFragment =new FriendFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_chat);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container,friendFragment).commit();

        //setFragment("chat");
    }

    /*public void setFragment(String name){
        Log.d("fragment",name);
        FragmentManager fragmentManager = getSupportFragmentManager();
        //프래그먼트의 변화를 관리하는 객체
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (name){
            case "chat":
                transaction.add(R.id.container, chattingFragment);
                break;
        }
        transaction.commit();
    }*/
}