package com.example.among.parents;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.among.R;
import com.example.among.chatting.ChattingFragment;
import com.example.among.chatting.model.Chat;
import com.example.among.children.childrenActivity;
import com.example.among.children.map.LocationMap;
import com.example.among.children.map.familyChat.FamilychatMainActivity;
import com.example.among.children.user.ProfileAdapter;
import com.example.among.function.MyReplaceFragment;
import com.example.among.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Parents extends AppCompatActivity {
    ImageView img;
    RecyclerView listView;
    Toolbar toolbar;
    Button button_insert_child;
    Button button_enter_chat;
    Context context;
    ParentsItemAdapter adapter;
    ArrayList<ParentsItem> list;
    ArrayList<ParentsItem> dataList;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents);

        listView = findViewById(R.id.parents);
        button_insert_child = findViewById(R.id.btn_insert_child);
        button_enter_chat = findViewById(R.id.btn_enter_chat);

        toolbar=findViewById(R.id.parents_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar();

        Bundle extras = getIntent().getExtras();
        userID = extras.getString("userID");
        Log.d("msg",userID);

        ParentsItem item = new ParentsItem(userID);
        HttpSelect task = new HttpSelect();
        task.execute(item);

        List<ParentsItem> itemList = new ArrayList<>();

        list = new ArrayList<ParentsItem>();
        dataList = new ArrayList<ParentsItem>();

        button_enter_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Parents.this, EnterChat.class);
                startActivity(intent);
            }
        });

        button_insert_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                View view = LayoutInflater.from(v.getContext()).inflate(R.layout.insert_child_box, null, false);
                builder.setView(view);

                final Button ButtonSubmit = (Button) view.findViewById(R.id.button_child_insert);
                final EditText insertChildName = (EditText) view.findViewById(R.id.insert_child_name);
                final EditText insertChildNumber = (EditText) view.findViewById(R.id.insert_child_number);

                ButtonSubmit.setText("삽입");
                final AlertDialog dialog = builder.create();
                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        String name = insertChildName.getText().toString();
                        String number = insertChildNumber.getText().toString();

                        ParentsItem item = new ParentsItem(userID, name, number, R.drawable.user);

                        HttpInsert task = new HttpInsert();
                        task.execute(item);

                        dialog.dismiss();

                    }
                });
                dialog.show();
            }

        });

        adapter = new ParentsItemAdapter(this, R.layout.parents_item,
                itemList);

        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 2);
        listView.setHasFixedSize(true);

        listView.setLayoutManager((manager));
        listView.setAdapter(adapter);
    }



    class HttpInsert extends AsyncTask<ParentsItem, Void, String> {

        @Override
        protected String doInBackground(ParentsItem... items) {
            URL url = null;
            JSONObject object = new JSONObject();
            String data = "";
            try {
                object.put("parents_user_id", items[0].getParents_user_id());
                object.put("name", items[0].getName());
                object.put("phone_number", items[0].getPhone_number());
                object.put("img",items[0].getImg());

                url = new URL("http://70.12.227.61:8088/among/child/insert");

                OkHttpClient client = new OkHttpClient();
                String json = object.toString();
                Log.d("msg", json);
                Request request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(MediaType.parse("application/json"), json))
                        .build();

                Response response = client.newCall(request).execute();
                data = response.body().string();
                Log.d("msg", data);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("true")) {
                Bundle extras = getIntent().getExtras();
                /*userID = extras.getString("userID");
                Log.d("msg",userID);*/

                Intent intent1 = new Intent(Parents.this, Parents.class);
                intent1.putExtra("userID", userID);

                startActivity(intent1);
            } else {
                Toast.makeText(Parents.this, "다시작성해 주세요", Toast.LENGTH_LONG).show();
            }
        }
    }

    class HttpSelect extends AsyncTask<ParentsItem, Void, String> {
        URL url = null;
        BufferedReader br = null;
        String result;
        JSONObject object = new JSONObject();
        @Override
        protected String doInBackground(ParentsItem... items) {
            try {
                object.put("parents_user_id",items[0].getParents_user_id());

                String path = "http://70.12.227.61:8088/among/child/selectAll";
                url = new URL(path);

                OkHttpClient client = new OkHttpClient();
                String json = object.toString();
                Log.d("msg",json);
                Request request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(MediaType.parse("application/json"),json))
                        .build();

                Response response = client.newCall(request).execute();
                result = response.body().string();
                Log.d("msg",result);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONArray ja = null;
            try {
                ja = new JSONArray(s);
                for (int i=0;i<ja.length();i++){
                    JSONObject object = ja.getJSONObject(i);
                    String parents_user_id = object.getString("parents_user_id");
                    String seq= object.getString("seq");
                    String name = object.getString("name");
                    String phone_number = object.getString("phone_number");
                    int img = object.getInt("img");

                    ParentsItem item = new ParentsItem(parents_user_id, seq, name, phone_number, img);
                    list.add(item);

                    if(!dataList.contains(list.get(i))){
                        dataList.add(item);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter = new ParentsItemAdapter(Parents.this, R.layout.activity_board_select, dataList);
            listView.setAdapter(adapter);
        }
    }
    //툴바 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_child_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_myLocation:{
                LocationMap();

                return true;
            }
            case R.id.action_sign_out:{
                signOut();
                return true;
            }
            case R.id.action_familyMaP:{
                //가족 지도
                popup();
                return true;
            }
            default:{
                return super.onOptionsItemSelected(item);
            }
        }

    }
    public void LocationMap(){
        Intent intent = new Intent(Parents.this, LocationMap.class);
        startActivity(intent);
    }
    //앱바 로그아웃
    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Parents.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    //앱바 지도보기
    public void popup(){
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(Parents.this);
        //AlertDialog의 타이틀을 정의
        builder.setTitle("데이터입력");

        //AlertDialog에 보여질 화면을 inflate
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.input, null);//뷰를 따로 만들어 작업

        //AlertDialog에 추가할 버튼을 정의
        builder.setPositiveButton("확인", new Parents.DialogListener());
        builder.setNegativeButton("취소",new Parents.DialogListener());
        //AlertDialog에 화면 설정. 붙이기
        builder.setView(dialogView);
        builder.show();
    }
    //다이얼로그
    class DialogListener implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialog, int which) {

            androidx.appcompat.app.AlertDialog inputAlert = (androidx.appcompat.app.AlertDialog) dialog;
            EditText input = inputAlert.findViewById(R.id.input);
            String data = input.getText().toString();

            String familyRoomName = "황가네";


            Intent intent = new Intent(Parents.this, FamilychatMainActivity.class);
            //intent에 공유할 데이터 저장
            intent.putExtra("Chatrooms", data);
            if (data.equals(familyRoomName)) {
                startActivity(intent);
            } else {
                Toast.makeText(Parents.this, "등록된 가족방이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}