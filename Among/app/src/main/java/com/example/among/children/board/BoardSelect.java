package com.example.among.children.board;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.among.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BoardSelect<AppCompatActivity> extends androidx.appcompat.app.AppCompatActivity {
    Toolbar toolbar;
    Button btn_comment_submit;
    EditText edit_comment_write;
    ArrayList<CommentItem> list;
    ArrayList<CommentItem> dataList;
    RecyclerView listView;
    CommentAdapter adapter;
    String userID;
    String board_num;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_select);

        toolbar=findViewById(R.id.board_insert_toolbar);
        toolbar.setTitle("게시판");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        String Title = "";
        String Text = "";
        String Time ="";

        final Bundle extras = getIntent().getExtras();

        Title = extras.getString("title");
        Text = extras.getString("text");
        Time = extras.getString("time");
        int num = extras.getInt("board_seq");
        Log.d("msg","board_num:::::"+board_num);
        userID = extras.getString("userID");
        final String board_num = num+"";

        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String getTime = simpleDateFormat.format(mDate);

        TextView textView_Title = (TextView) findViewById(R.id.textView_Title);
        TextView textView_Text = (TextView) findViewById(R.id.textView_Text);
        TextView textView_Time = (TextView) findViewById(R.id.textView_Time);
        ImageView imageView = (ImageView) findViewById(R.id.imageView_seq);
        listView = (RecyclerView) findViewById(R.id.recycler_commentview);

        textView_Title.setText(Title);
        textView_Text.setText(Text);
        textView_Time.setText(Time);
        imageView.setImageResource(R.drawable.user);

        registerForContextMenu(listView);

        CommentItem item = new CommentItem(board_num);
        HttpSelect task = new HttpSelect();
        task.execute(item);

        adapter = new CommentAdapter(BoardSelect.this, dataList, R.layout.activity_board_select, userID);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(manager);
        listView.setAdapter(adapter);


        list = new ArrayList<CommentItem>();
        dataList = new ArrayList<CommentItem>();
        btn_comment_submit = findViewById(R.id.comment_submit);
        edit_comment_write = findViewById(R.id.comment_write);

        btn_comment_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long now = System.currentTimeMillis();
                Date mDate = new Date(now);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                String getTime = simpleDateFormat.format(mDate);

                String text = edit_comment_write.getText().toString();
                Log.d("msg","board_num:"+board_num);
                CommentItem item = new CommentItem(text, getTime, userID, board_num);
                HttpInsert task = new HttpInsert();
                task.execute(item);
            }
        });

        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(manager);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item ){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class HttpInsert extends AsyncTask<CommentItem, Void, String> {

        @Override
        protected String doInBackground(CommentItem... commentItems) {
            URL url = null;
            JSONObject object = new JSONObject();
            String data = "";
            try {
                object.put("mcomment", commentItems[0].getComment());
                object.put("writedate", commentItems[0].getDate());
                object.put("user_id",commentItems[0].getUserid());
                object.put("board_num",commentItems[0].getBoard_num());
                url = new URL("http://192.168.219.106:8088/among/board/comment/insert");

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
                Toast.makeText(BoardSelect.this, "댓글이 정상적으로 삽입되었습니다.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(BoardSelect.this, "다시작성해 주세요", Toast.LENGTH_LONG).show();
            }
        }
    }

    class HttpSelect extends AsyncTask<CommentItem, Void, String> {
        URL url = null;
        BufferedReader br = null;
        String result;
        JSONObject object = new JSONObject();
        @Override
        protected String doInBackground(CommentItem... commentItems) {
            try {
                object.put("board_num",commentItems[0].getBoard_num());

                String path = "http://192.168.219.106:8088/among/board/comment/selectAll";
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
                    int num = object.getInt("comment_seq");
                    String board_num = object.getString("board_num");
                    String userid = object.getString("user_id");
                    String mcomment = object.getString("mcomment");
                    String time = object.getString("writedate");
                    Log.d("msg","userid:"+userid+", mcomment:"+mcomment+", time:"+time);

                    CommentItem item = new CommentItem(num, mcomment,time,userid,board_num);
                    list.add(item);

                    if(!dataList.contains(list.get(i))){
                        dataList.add(item);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter = new CommentAdapter(BoardSelect.this, dataList, R.layout.activity_board_select, userID);
            listView.setAdapter(adapter);

        }
    }
}
