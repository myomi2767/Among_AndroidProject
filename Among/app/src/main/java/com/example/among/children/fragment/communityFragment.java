package com.example.among.children.fragment;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.among.R;
import com.example.among.children.board.CommunityListViewAdapter;
import com.example.among.children.board.CommunityListViewItem;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link communityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link communityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class communityFragment extends Fragment{
    ArrayList<CommunityListViewItem> list;
    ArrayList<CommunityListViewItem> filteredList;
    CommunityListViewAdapter adapter;
    private RecyclerView listView;
    Button fab;
    Spinner spinner;
    EditText board_edit_search;
    String userid;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    public communityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SmsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static communityFragment newInstance(String param1, String param2) {
        communityFragment fragment = new communityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        list = new ArrayList<CommunityListViewItem>();
        HttpSelect task = new HttpSelect();
        task.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View viewGroup = (View) LayoutInflater.from(getContext()).inflate(R.layout.fragment_community, container, false);
        listView = (RecyclerView) viewGroup.findViewById(R.id.recycler_listview);
        fab = viewGroup.findViewById(R.id.board_insert);
        spinner = viewGroup.findViewById(R.id.board_spinner);
        board_edit_search = viewGroup.findViewById(R.id.edit_search);
        registerForContextMenu(listView);

        //childernActivity에서 userID 받아오기
        Bundle bundle = getArguments();
        userid = bundle.getString("userID");
        Log.d("msg","fra안"+userid);

        filteredList = new ArrayList<>();
        adapter = new CommunityListViewAdapter(communityFragment.this, list, R.layout.fragment_community, userid);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(manager);
        listView.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "선택된 아이템 : "+spinner.getItemAtPosition(position),Toast.LENGTH_LONG).show();

                String str =  parent.getItemAtPosition(position).toString();

                /*switch(str){
                    case "제목": // 제목으로 검색
                        board_edit_search.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                adapter.getFilter2().filter(s);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                    case "내용": // 내용으로 검색
                        board_edit_search.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                adapter.getFilter().filter(s);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        board_edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view = LayoutInflater.from(getContext()).inflate(R.layout.board_edit_box, null, false);
                builder.setView(view);

                final Button ButtonSubmit = (Button) view.findViewById(R.id.button_edit_submit);
                final EditText editTextTitle = (EditText) view.findViewById(R.id.editView_title);
                final EditText editTextText = (EditText) view.findViewById(R.id.editView_text);

                ButtonSubmit.setText("삽입");
                final AlertDialog dialog = builder.create();

                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        long now = System.currentTimeMillis();
                        Date mDate = new Date(now);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                        String getTime = simpleDateFormat.format(mDate);

                        String title = editTextTitle.getText().toString();
                        String text = editTextText.getText().toString();

                        CommunityListViewItem item = new CommunityListViewItem(title, text, getTime, userid);
                        HttpInsert task = new HttpInsert();
                        task.execute(item);

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        return viewGroup;
    }

    class HttpInsert extends AsyncTask<CommunityListViewItem, Void, String> {

        @Override
        protected String doInBackground(CommunityListViewItem... boardItems) {
            URL url = null;
            JSONObject object = new JSONObject();
            String data = "";
            try {
                object.put("title", boardItems[0].getTitle());
                object.put("text", boardItems[0].getText());
                object.put("write_date", boardItems[0].getDate());
                object.put("user_id",boardItems[0].getUserid());

                url = new URL("http://192.168.219.106:8088/among/board/insert");

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
                Toast.makeText(getContext(), "삽입되었습니다.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "다시작성해 주세요", Toast.LENGTH_LONG).show();
            }
        }
    }

    class HttpSelect extends AsyncTask<Void, Void, String> {
        URL url = null;
        BufferedReader br = null;
        String data = "";
        @Override
        protected String doInBackground(Void... voids) {
            try {
                String path = "http://192.168.219.106:8088/among/board/selectAll";
                url = new URL(path);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");

                if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
                    data = br.readLine();
                    Log.d("msg",data);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONArray ja = null;
            try {
                ja = new JSONArray(s);
                for (int i=0;i<ja.length();i++){
                    JSONObject object = ja.getJSONObject(i);
                    int idx = object.getInt("board_seq");
                    String title = object.getString("title");
                    String text = object.getString("text");
                    String time = object.getString("write_date");

                    CommunityListViewItem item = new CommunityListViewItem(idx,title,text,time);
                    list.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter = new CommunityListViewAdapter(communityFragment.this, list, R.layout.fragment_community, userid);
            listView.setAdapter(adapter);

        }
    }
}


