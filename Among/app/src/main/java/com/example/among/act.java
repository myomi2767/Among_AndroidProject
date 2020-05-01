package com.example.among;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.among.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class act extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act);
        getToken();
    }
    /*public void request(View view){
        new rquestThread("2").start();
    }*/
    //토큰을 생성하고 만드는 작업
    public void getToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        //토큰을 가져오다 실패하면 실행하게 된다.
                        if (!task.isSuccessful()) {
                            Log.d("myfcm", "getInstanceId failed", task.getException());
                            return;
                        }
                        String token = task.getResult().getToken();
                        Log.d("myfcm", token);

                        /*new SendTokenThread(token).start();*/
                    }
                });
    }
/*
    class SendTokenThread extends Thread{
        String token;
        public SendTokenThread(String token) {
            this.token = token;
        }

        @Override
        public void run() {
            super.run();
            try {
                OkHttpClient client = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                builder = builder.url("http://70.12.224.58:8088/bigdataShop/fcm/fcm_check?token="+token);
                Request request = builder.build();
                Call newcall = client.newCall(request);
                newcall.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class rquestThread extends Thread{
        String id;
        public rquestThread(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            super.run();
            try {
                OkHttpClient client = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                builder = builder.url("http://70.12.224.58:8088/bigdataShop/fcm/sendClient?id="+id);
                Request request = builder.build();
                Call newcall = client.newCall(request);
                newcall.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/
}