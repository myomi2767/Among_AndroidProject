package com.example.among.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.among.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SignUp extends AppCompatActivity {
    EditText userID;
    EditText name;
    EditText password;
    EditText phone;
    EditText birth;
    Button register;
    Button idChk;
    RadioGroup radioGroup;

    MemberDTO member;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup2);
        userID = findViewById(R.id.userID);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        birth = findViewById(R.id.birth);
        radioGroup = (RadioGroup) findViewById(R.id.radio1);

        register = findViewById(R.id.register);
        idChk = findViewById(R.id.certificate);

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
                        new SendTokenThread(token).start();
                    }
                });
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton rb = findViewById(radioGroup.getCheckedRadioButtonId());

                String id = userID.getText().toString();
                String na = name.getText().toString();
                String pass = password.getText().toString();
                String num = phone.getText().toString();
                String bir = birth.getText().toString();
                String gen = rb.getTag().toString();

                member = new MemberDTO(id, na, pass, num, bir, gen);

                HttpInsert task = new HttpInsert();
                task.execute(member);
            }
        });

        idChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = userID.getText().toString();

                member = new MemberDTO(id);
                HttpIDChk task = new HttpIDChk();
                task.execute(member);
            }
        });
    }

    class HttpInsert extends AsyncTask<MemberDTO, Void, String>{

        @Override
        protected String doInBackground(MemberDTO... memberDTOS) {
            URL url = null;
            //JSONObject object = new JSONObject();
            BufferedReader br = null;
            String data;
            try {
                /*object.put("userID",memberDTOS[0].getUserID());
                object.put("name",memberDTOS[0].getName());
                object.put("password",memberDTOS[0].getPassword());
                object.put("phone",memberDTOS[0].getPhone());
                object.put("birth",memberDTOS[0].getBirth());
                object.put("gender",memberDTOS[0].getGender());*/
                url = new URL("http://70.12.227.61:8088/among/member/insert.do");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

                OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
                data = "userID="+memberDTOS[0].getUserID()+"&name="+memberDTOS[0].getName()
                    +"&password="+memberDTOS[0].getPassword()+"&phone="+memberDTOS[0].getPhone()
                    +"&birth="+memberDTOS[0].getBirth()+"&gender="+memberDTOS[0].getGender();
                osw.write(data);
                osw.flush();
                if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()
                            ,"UTF-8"));
                    Log.d("myhttp",data);
                    Intent intent = new Intent(SignUp.this,LoginActivity.class);
                    startActivity(intent);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    class HttpIDChk extends AsyncTask<MemberDTO, Void, String>{

        @Override
        protected String doInBackground(MemberDTO... memberDTOS) {
            URL url = null;
            BufferedReader br = null;
            String data;
            try {
                url = new URL("http://70.12.227.61:8088/among/member/chk.do");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

                OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
                data = "userID="+memberDTOS[0].getUserID();
                osw.write(data);
                osw.flush();

                if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()
                            ,"UTF-8"));
                    br.readLine();
                    //Log.d("myhttp",);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }
}
