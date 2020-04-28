package com.example.among.function;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.among.R;


public class boardWrite extends AppCompatActivity {
    Spinner spinner_sort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_write);

        spinner_sort = findViewById(R.id.spinner_sort);
        ArrayAdapter sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_sort.setAdapter(sortAdapter);

        Intent intent = getIntent();


    }
    public void btn_register(View view){

    }
}
