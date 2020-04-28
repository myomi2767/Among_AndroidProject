package com.example.among;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button parentButton;
    Button childButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        childButton = findViewById(R.id.child);
        parentButton = findViewById(R.id.parent);




    }
    public void modeClick(View view){

    }
}
