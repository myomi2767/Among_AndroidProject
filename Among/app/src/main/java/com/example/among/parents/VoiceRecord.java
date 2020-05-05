package com.example.among.parents;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.among.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VoiceRecord extends AppCompatActivity {
    NotificationManager notificationManager;

    MediaRecorder recorder;
    boolean isRecording;
    FirebaseTest fcm;
    Long Time;
    String mTime;

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


    /*    Time = System.currentTimeMillis();
        Date time = new Date(Time);
        Random rand = new Random();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        mTime = simpleDateFormat.format(time)+"";*/

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

      /*  String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();

        filename = sdcard+File.separator+"recorded"+mTime+".mp4";*/
        Log.d("msg","filename(경로)"+filename);
        //final String str = "recorded_"+mTime+".mp4";

        if(ActivityCompat.checkSelfPermission((VoiceRecord.this), Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(VoiceRecord.this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 1000);
            Log.d("per","퍼미션 없음");
        }{
            Log.d("per","퍼미션 있음");
        }

        TransferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(VoiceRecord.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(VoiceRecord.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
                } else {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("method", "fileUpload");
                    map.put("filename","recorded"+mTime+".mp4");
                    FileTask networkTask = new FileTask();
                    networkTask.execute(map);
                }

                btn_pending_click(v);
            }
        });
    }
    public void startRecording(){

        Date time = new Date();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        mTime = simpleDateFormat.format(time)+"";
        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();

        filename = sdcard+File.separator+"recorded"+mTime+".mp4";
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecording(){
        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();

        filename = sdcard+File.separator+"recorded"+mTime+".mp4";
        if(recorder ==null){
            return;
        }
        recorder.stop();
        recorder.release();
        recorder = null;
        ContentValues values = new ContentValues(10);
        Log.d("msg",mTime);
        Toast.makeText(this.getApplicationContext(),"시간:"+mTime,Toast.LENGTH_LONG).show();

        values.put(MediaStore.MediaColumns.TITLE, "recorded");
        values.put(MediaStore.Audio.Media.ALBUM, "Audio Album");
        values.put(MediaStore.Audio.Media.ARTIST,"parents");
        values.put(MediaStore.Audio.Media.DISPLAY_NAME,"Recorded Audio");
        values.put(MediaStore.Audio.Media.IS_RINGTONE,1);
        values.put(MediaStore.Audio.Media.IS_MUSIC,1);
        values.put(MediaStore.MediaColumns.DATE_ADDED,System.currentTimeMillis()/1000);
        values.put(MediaStore.MediaColumns.MIME_TYPE,"audio/mp4");
        values.put(MediaStore.Audio.Media.DATA, filename);

        Uri audioUri = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);

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

    //**************************NOTIFICATION***************************

    public void btn_pending_click(View view){
        NotificationCompat.Builder builder= getBuilder("pending","pending_intent");
        builder.setSmallIcon(android.R.drawable.ic_notification_overlay);
        builder.setContentTitle("Among - 음성메시지 도착");
        builder.setContentText("자 클릭하세요 액티비티가 실행됩니다.");
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.alarm);
        builder.setLargeIcon(bitmap);
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
        //notification을 클릭하면 액티비티를 실행할 수 있도록 인텐트를 만든다.
        Intent intent = new Intent(this,VoiceNoti.class);
        //Intent를 실행할 수 있도록 의뢰하기 위해 PendingIntent를 생성
        //Flag_Cancle_current : 이전에 생성한 PendingIntent는 취소하고 다시 만든다.
        //FLAG_UPDATE_CURRENT : 현재의 내용으로 이전 객체 업데이트
        //Flag_no_create : PendingIntent를 새롭게 만들지 않고 이미 생성된 PendingIntent를 그대로 획득해서 사용하기
        //Flag_one_shot : 한 번만 pendingIntent를 만들기 위해 사용
        PendingIntent pendingIntent = PendingIntent.getActivity(this,10,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        //Builder에게 PendingIntent설정
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notificationManager.notify(1005,notification);
    }

    public void btn_click(View view){
        NotificationCompat.Builder builder= getBuilder("channel_1","data manager");
        //안드로이드 이미지 설정
        builder.setSmallIcon(android.R.drawable.ic_notification_overlay);
        //제목설정
        builder.setContentTitle("Notification연습");
        builder.setContentText("지금 바쁘고 힘드시죠? 모두 행복하세요!~~~~");
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.alarm);
        builder.setLargeIcon(bitmap);
        //알림과 동시에 소리와 스마트폰에 진동이 울리도록 할 수 있다.
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);

        //Notification객체 만들기
        Notification notification = builder.build();
        //Notification을 상태바에 등록하기 - id는 알림을 식별하기 위해 사용하며 발생된 notification을 보통은 사용자가
        //알림을 클릭하거나 밀어서 사라지게 만들 수 있지만 프로그램 안에서 취소해야 하는 경우 식별해야 하므로
        notificationManager.notify(1004,notification);
    }

    //Notification객체는 직접 생성되지 않고 NotificationCompat.Builder를 이용해서 생성한다.
    //Notification은 안드로이드 버전별로 많이 바뀌었다. 이렇게 자주 바뀌면 호환성에 문제가 생길텐데
    //따라서 Support라이브러리에서 NotificationCompat을 제공한다.
    //Notification Channel이라는 것을 이용해서 알림메시지에 대한 관리를 한다.
    //알림이 네트워크에 관련된 것인지 , 혹은 저장에 관련된 것인지 이런 부분들을 정의하고 작업할 수 있다.
    public NotificationCompat.Builder getBuilder(String channel_id,String channel_name){
        //낮은 버전을 사용하기 위한 사용자가 있는 경우  - 8.0부터 채널을 통해 관리함
        notificationManager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder= null;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){ //안드로이드 8.0오레오 버전인경우
            NotificationChannel channel =
                    new NotificationChannel(channel_id,channel_name,NotificationManager.IMPORTANCE_HIGH);

            //알림메니저를 통해 채널을 등록한다. - Builder에 의해 만들어진 Notification은 체널에 의해 관리된다.
            notificationManager.createNotificationChannel(channel);
        }else{
            //이전 버전은 옛날방식으로 Builder를 얻어오기
            builder = new NotificationCompat.Builder(this,channel_id);
        }
        return builder;
    }

}