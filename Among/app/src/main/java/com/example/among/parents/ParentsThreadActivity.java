package com.example.among.parents;/*
package com.example.among.parents;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.BufferedWriter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.among.R;

public class MyThreadActivity extends Activity {
    TextView textView01;
    //TextHandler handler;
    Handler handler;
    private Socket socket; // 소켓 정의
    public static String ip = null;
    public static int port = 0;

    EditText edit, edit_ip, edit_port;
    public static String msg;
    public static boolean Enable = false;

    */
/**
     * Called when the activity is first created.
     *//*

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_record);
        edit = (EditText) findViewById(R.id.editText3); //아이디를 미리 지정
        edit_ip = (EditText) findViewById(R.id.editText1); //아이디를 미리 지정
        edit_port = (EditText) findViewById(R.id.editText2); //아이디를 미리 지정
        textView01 = (TextView) findViewById(R.id.text);

        Button button01 = (Button) findViewById(R.id.connect);
        button01.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (Enable == false) {
                    ip = edit_ip.getText().toString();
                    port = Integer.parseInt(edit_port.getText().toString());
                    connect();
                    Enable = true;
                    RequestThread rt = new RequestThread();
                    rt.start();
                    SendThread st = new SendThread();
                    st.start();
                }
            }
        });

        textView01 = (TextView) findViewById(R.id.text);

        Button button02 = (Button) findViewById(R.id.exit);
        button02.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Enable = false;
                try {

                    socket.close();
                    Log.i("", "소켓 닫음");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.i("", "무한루프 종료");
            }
        });
        Button button03 = (Button) findViewById(R.id.send);
        button03.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String str = edit.getText().toString();  //str에 에디트텍스트값 넣음
                try {
                //파일전송 및 통신★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆★☆

                    Log.i("로그", "두번째이상 클릭");         //★★★★★★★로그켓
                    str = edit.getText().toString(); //str변수에 에디트텍스트값 넣음
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "MS949")), true);
                    out.println(str);
                    Log.i("로그", "보냄");         //★★★★★★★로그켓
                    out.flush();
                    File f = new File(new String(("/mnt/sdcard/1.jpg")));

                    DataInputStream dis = new DataInputStream(new FileInputStream(new File("/mnt/sdcard/1.jpg")));
                    long length = f.length();
                    DataOutputStream dos = new
                            DataOutputStream(socket.getOutputStream());
                    byte[] buf = new byte[(int) length];
                    Log.i("로그", "" + length);       //★★★★★★★로그켓
                    while (dis.read(buf) > 0) {
                        dos.write(buf, 0, 5370);
                        Log.i("로그", "보냄");         //★★★★★★★로그켓
                    }
                    dos.flush();
                    dos.close();
                } catch (IOException e) {
                    Log.i("로그", "두번째이상 클릭에서 오류");         //★★★★★★★로그켓
                }
            }
        });


        handler = new Handler();
    }


    public void connect() {
        if (Enable == true) {
            try {
                socket = new Socket(ip, port);
                Log.i("로그", "서버열림");         //★★★★★★★로그켓

            } catch (Exception ex) {
            }
        }
    }


    class SendThread extends Thread {
        public void run() {

            while (Enable == true) {
                try {
                    Log.i("로그", "버퍼라이터 정의");         //★★★★★★★로그켓
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "MS949"));
                    Log.i("로그", "리드라인 시작");         //★★★★★★★로그켓
                    //msg = br.readLine();  // 서버에서 올 메세지를 기다린다.
                    msg = new String(br.readLine());
                    Log.i("로그", "리드라인 끝, 읽어온 버퍼:" + msg);         //★★★★★★★로그켓


                } catch (Exception ex) {
                    Log.i("로그", "리드라인 오류 또는 타임아웃");//★★★★★★★로그켓

                    connect();
                    Log.i("로그", "소켓 재생성 완료");//★★★★★★★로그켓

                }
            }

        }
    }


    class RequestThread extends Thread {
        public void run() {
            while (Enable == true) {

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (msg != null) {
                    Log.i("로그", "포스트 보냄");         //★★★★★★★로그켓
                    handler.post(new TextRunnable(msg));
                    msg = null;
                }
            }
        }
    }


    class TextRunnable implements Runnable {
        String mText;

        public TextRunnable(String text) {
            mText = text;
        }

        public void run() {
            Log.i("로그", "핸들러-텍스트 바꿈 :" + mText);         //★★★★★★★로그켓
            Log.i("로그", "현재 버퍼 :" + msg);         //★★★★★★★로그켓

            textView01.setText(mText);
        }
    }

}*/
