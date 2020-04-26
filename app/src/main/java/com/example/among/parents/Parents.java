package com.example.among.parents;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import com.example.among.R;

public class Parents extends AppCompatActivity {
    ImageView img;
    RecyclerView list;
    ToggleButton toggleButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents);

        list = findViewById(R.id.parents);
        toggleButton = findViewById(R.id.toggleButton);

        List<ParentsItem> itemList = new ArrayList<>();

        ArrayList<ParentsItem> item = new ArrayList<ParentsItem>();

        item.add(new ParentsItem("자녀 1", R.drawable.user));
        item.add(new ParentsItem("자녀 2", R.drawable.user));
        item.add(new ParentsItem("자녀 3", R.drawable.user));
        item.add(new ParentsItem("자녀 4", R.drawable.user));
        item.add(new ParentsItem("자녀 5", R.drawable.user));

        for(int i=0; i<item.size(); i++){
            itemList.add(item.get(i));
        }

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Parents.this, FamilyChat.class);
                startActivity(intent);
            }
        });


        ParentsItemAdapter adapter = new ParentsItemAdapter(this, R.layout.farents_item,
                itemList);
        /*LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);*/

        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 2);
        list.setHasFixedSize(true);

        list.setLayoutManager((manager));
        list.setAdapter(adapter);
    }
}
