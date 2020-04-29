package com.example.among.parents;

import android.content.ContentValues;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.among.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class VoiceRecord extends AppCompatActivity {
    //public static final String AUDIO_URL = ""
    MediaRecorder recorder;
    boolean isRecording;
    FirebaseTest fcm;

    MediaPlayer mediaPlayer;
    int position = 0;

    String filename;

    Button RecordBtn;
    Button PlayBtn;
    Button StopBtn;
    Button ResumeBtn;
    Button RestartBtn;
    Button TransferBtn;

    //사용자 단말기의 토큰과 아이디
    String token;
    String userID;

    TextView textView;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_record);

        RecordBtn = findViewById(R.id.start);
        PlayBtn = findViewById(R.id.play);
        StopBtn = findViewById(R.id.stop);
        ResumeBtn = findViewById(R.id.resume);
        RestartBtn = findViewById(R.id.restart);
        TransferBtn = findViewById(R.id.transfer);
        textView = findViewById(R.id.txtView);

        RecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRecording==false){
                    startRecording();
                    isRecording = true;
                    RecordBtn.setText("Stop Recording");
                }else{
                    stopRecording();
                    isRecording=false;
                    RecordBtn.setText("Start Recording");
                }
            }
        });

        PlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio(filename);
                Toast.makeText(getApplicationContext(),"음악 파일 재생 시작됨",Toast.LENGTH_LONG).show();
            }
        });

        StopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer!=null){
                    mediaPlayer.stop();
                    Toast.makeText(getApplicationContext(),"음악 파일 재생 중지됨",Toast.LENGTH_LONG).show();
                }
            }
        });

        ResumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer!=null){
                    position = mediaPlayer.getCurrentPosition();
                    mediaPlayer.pause();
                    Toast.makeText(getApplicationContext(),"음악 파일 재생 일시정지됨",Toast.LENGTH_LONG).show();
                }
            }
        });

        RestartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer!=null && !mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                    mediaPlayer.seekTo(position);
                    Toast.makeText(getApplicationContext(),"음악 파일 재생 재시작됨",Toast.LENGTH_LONG).show();
                }
            }
        });

        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        filename = sdcard + File.separator+"recorded.mp4";

        TransferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getToken();
                Thread sendThread = new Thread(new SendTokenThread(token));
                Thread requestThread = new Thread(new rquestThread(userID));
                sendThread.start();
                requestThread.start();
                Toast.makeText(getApplicationContext(),"서버로 보내는 키: "+token,Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),"서버로 보내는 아이디: "+userID,Toast.LENGTH_LONG).show();
            }
        });
    }
    public void startRecording(){
        if(recorder==null){
            recorder= new MediaRecorder();
        }

        //음질 향상 코드
        recorder.setAudioSamplingRate(44100);
        recorder.setAudioEncodingBitRate(96000);

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // MIC상수설정
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); //인코더 방법
        recorder.setOutputFile(filename); //filename으로 파일이 출력(저장) // recorded.mp4

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording(){
        if(recorder ==null){
            return;
        }
        recorder.stop();
        recorder.release();
        recorder = null;
        ContentValues values = new ContentValues(10);

        values.put(MediaStore.MediaColumns.TITLE, "Recorded");
        values.put(MediaStore.Audio.Media.ALBUM, "Audio Album");
        values.put(MediaStore.Audio.Media.ARTIST,"parents");
        values.put(MediaStore.Audio.Media.DISPLAY_NAME,"Recorded Audio");
        values.put(MediaStore.Audio.Media.IS_RINGTONE,1);
        values.put(MediaStore.Audio.Media.IS_MUSIC,1);
        values.put(MediaStore.MediaColumns.DATE_ADDED,System.currentTimeMillis()/1000);
        values.put(MediaStore.MediaColumns.MIME_TYPE,"audio/mp4");
        values.put(MediaStore.Audio.Media.DATA, filename);

        Uri audioUri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

        uploadAudio(); // 파일 서버에 업로드하기?

        if(audioUri == null){
            Log.d("SampleAudioRecorder","Audio insert failed.");
            return;
        }
    }

    public void playAudio(String url){
        killMediaPlayer();
        mediaPlayer= new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadAudio(){
        Uri uri = Uri.fromFile(new File(filename));
    }

    protected void onDestroy(){
        super.onDestroy();
        killMediaPlayer();
    }

    private void killMediaPlayer(){
        if(mediaPlayer!=null){
            try{
                mediaPlayer.release();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void printClientLog(final String data){
        Log.d("VoiceRecord",data);
        handler.post(new Runnable() {
            @Override
            public void run() {
                textView.append("클라이언트 로그 : "+data+"\n");
            }
        });
    }
    public void printServerLog(final String data){
        Log.d("VoiceRecord",data);
        handler.post(new Runnable() {
            @Override
            public void run() {
                textView.append("서버 로그 : "+data+"\n");
            }
        });
    }

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
                        token = task.getResult().getToken();
                        Log.d("myfcm", token);
                        textView.setText(token);
                        /*new SendTokenThread(token).start();*/
                    }
                });
        // Token : dfTpRsr2y3M:APA91bHH7ZPr6ioN8JECZFzpiEkF86IKRp3Qgjo0IoE3Uf_K7vJBye0yif-GILfNceAmyUjg2mCdoa18_KzbYbLs1YnQQnc6IBGZaFcO3jMZ2K_UQcV-V41FXec0z9gO3qG5tFi7oC7n
    }

    class SendTokenThread extends Thread{
        String token;
        public SendTokenThread(String token) {
            this.token = token;
        }

        @Override
        public void run() {
            super.run();
            try {
                OkHttpClient client = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                builder = builder.url("http://192.168.249.1:8088/among/fcm/fcm_check?token="+token);
                Request request = builder.build();
                Call newcall = client.newCall(request);
                newcall.execute();
                Log.d("fcm","서버 전송 토큰: "+token);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class rquestThread extends Thread{
        String id;
        public rquestThread(String id) {
            this.id = id;
        }

        @Override
        public void run() {
            super.run();
            try {
                OkHttpClient client = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                builder = builder.url("http://192.168.249.1:8088/among/fcm/sendClient?id="+id);
                Request request = builder.build();
                Call newcall = client.newCall(request);
                newcall.execute();
                Log.d("fcm","서버 전송 아이디: "+id);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class VoiceThread extends Thread{
        String voiceFile = filename;

    }

}