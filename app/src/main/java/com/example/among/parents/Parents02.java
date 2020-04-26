package com.example.among.parents;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.example.among.R;

public class Parents02 extends AppCompatActivity {
    Button close_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents02);
        close_btn = findViewById(R.id.close_btn);
        final Intent intent = getIntent();

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //종료시 putExtra 추가 기능을 위한 값을 전달해주기


                finish();
            }
        });
    }
}
