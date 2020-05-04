package com.example.among;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.among.login.DBHandler;
import com.example.among.login.LoginActivity;
import com.example.among.login.ModeDBHelper;

public class MainActivity extends AppCompatActivity {
    boolean permission_state;
    Button parentButton;
    Button childButton;
    DBHandler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            permission_state = true;
            printToast("권한이 설정되었습니다.");
        }else{
            printToast("권한이 없습니다.");
            //2. 권한이 없는 경우 권한을 설정하라는 메시지를 띄운다.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET,
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            printToast("권한이 설정되었습니다.");


        }
        handler = new DBHandler(this);
        if(handler.select().getCount()!=0){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_main);
        childButton = findViewById(R.id.child);
        parentButton = findViewById(R.id.parent);

        //자녀모드 선택 시 insert
        childButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.insert(0);
                Toast.makeText(getApplicationContext(),"자녀모드 선택 및 삽입성공",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //부모님모드 선택 시 insert
        parentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.insert(1);
                Toast.makeText(getApplicationContext(),"부모님모드 선택 및 삽입성공",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void printToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }
}
