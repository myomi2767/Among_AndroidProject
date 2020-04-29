package com.example.among.parents;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;

import com.example.among.R;

public class Parents02 extends AppCompatActivity {
    Button close_btn;
    //승인받을 권한의 목록
    String[] permission_list = {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //String strPhoneNumber = "tel:"+personList.get(position).get(TAG_MOBILE); // - 자녀가 DB로 들어오면 해당 자녀의 전화번호 가져오기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parents02);
        close_btn = findViewById(R.id.close_btn);
        final Intent intent = getIntent();

        runPermission();

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //종료시 putExtra 추가 기능을 위한 값을 전달해주기
                finish();
            }
        });
    }

    //권한을 체크 - 승인처리
    public void runPermission(){
        //하위버전이면 실행되지 않도록 처리
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return; //종료
        }
        //모든 권한을 셀프체크
        for(String permission:permission_list){
            int chk = checkCallingOrSelfPermission(permission);
            if(chk== PackageManager.PERMISSION_DENIED){
                requestPermissions(permission_list, 0);
                break;
            }
        }
    }

    //음성 통화 걸기 메소드
    public void runCallPhone(View v){
        Intent intent = null;
        int chk = PermissionChecker.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if(chk== PackageManager.PERMISSION_GRANTED){
            Log.d("tel","성공");
            intent = new Intent(Intent.ACTION_CALL,
                    Uri.parse("tel:01072971287"));
        }else{
            Log.d("tel","실패");
            return;
        }
        startActivity(intent);
    }

    //영상 통화 걸기 메소드
    public void runVideoCallPhone(View v){
        Intent intent = null;
        int chk = PermissionChecker.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if(chk== PackageManager.PERMISSION_GRANTED){
            intent = new Intent(Intent.ACTION_CALL,
                    Uri.parse("tel:01072971287"));
            intent.putExtra("andorid.phone.extra.calltype",0);
            //intent.putExtra("videocall",true);
        }else{
            Log.d("tel","실패");
            return;
        }
        startActivity(intent);
    }

    public void runVoiceMail(View v){
        Context context = v.getContext();
        Intent intent = new Intent(v.getContext(), VoiceRecord.class);
        context.startActivity(intent);
    }

}
