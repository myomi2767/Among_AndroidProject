package com.example.among;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
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
    Button parentButton;
    Button childButton;
    DBHandler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}
