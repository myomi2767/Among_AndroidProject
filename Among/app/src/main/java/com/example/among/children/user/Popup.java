package com.example.among.children.user;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;

import com.example.among.R;

public class Popup extends AppCompatActivity {
    private Context context;

    //승인받을 권한의 목록
    String[] permission_list = {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public void popup(){
        final Dialog MyDialog = new Dialog(context);

        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.activity_click_function);
        //runPermission();

        Button runCall = (Button)MyDialog.findViewById(R.id.runCall);
        Button videoCall = (Button)MyDialog.findViewById(R.id.videoCall);
        Button close = (Button)MyDialog.findViewById(R.id.btn_back);
        Button msg = (Button)MyDialog.findViewById(R.id.SendMsg);

        //전화
        runCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
        //영통
        videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "영상통화",Toast.LENGTH_SHORT).show();

            }
        });
        //닫기
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.cancel();
            }
        });
        //메세지
        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "메세지",Toast.LENGTH_SHORT).show();
            }
        });
        MyDialog.show();
    }

    public void runCall(){
        Toast.makeText(context, "전화",Toast.LENGTH_SHORT).show();
        //runPermission();

        Intent intent1 = null;
        int chk = PermissionChecker.checkSelfPermission(context,
                Manifest.permission.CALL_PHONE);
        if(chk== PackageManager.PERMISSION_GRANTED){
            //권한 승락이 된 상태                           //5554번에서 5556번 전화하기
            Log.d("tel","성공");
            intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:010-8231-0841"));

        }else {
            Log.d("tel","실패");
            return;
        }
        context.startActivity(intent1);
    }

    //영상 통화 걸기 메소드
    public void runVideoCallPhone(View v){
        Intent intent = null;
        int chk = PermissionChecker.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
        if(chk== PackageManager.PERMISSION_GRANTED){
            intent = new Intent(Intent.ACTION_CALL,
                    Uri.parse("tel:01072971287"));
            intent.putExtra("andorid.phone.extra.calltype",0);
            //intent.putExtra("videocall",true);
        }else{
            Log.d("tel","실패");
            return;
        }
        context.startActivity(intent);
    }

    //권한을 체크할 메서드 : 승인처리
    public void runPermission(){
        //하위버전이면 실행되지 않도록 처리
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return; // 이러면 그냥 종료
        }
        //모든 권한을 셀프체크  permission_list에서 하나씩 꺼내서 작업
        for(String permission:permission_list){
            int chk = checkCallingOrSelfPermission(permission);
            //권한 셀프 체크가 안되는 경우에
            if(chk== PackageManager.PERMISSION_DENIED){
                requestPermissions(permission_list,0);
                break;
            }

        }
    }
}
