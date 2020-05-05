package com.example.among.children.user;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.RecyclerView;

import com.example.among.R;
import com.example.among.children.map.LocationMap;

import java.util.List;

public class ProfileAdapter
        extends RecyclerView.Adapter<ProfileAdapter.CustomViewHolder> {

    private Context context;
    private int resId;
    private List<ProfileItem> datalist;

    public ProfileAdapter(Context context, int resId, List<ProfileItem> datalist) {
        this.context = context;
        this.resId = resId;
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(resId,null);
        //View view = LayoutInflater.from(context).inflate(R.layout.activity_child_first_page_row,parent,false);
        //CustomViewHolder holder = new CustomViewHolder(view);
        return new CustomViewHolder(view);
    }

    //목록: 프로필, 이름, 대화명, 지도
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        ImageView myImg =holder.myImg;
        myImg.setImageResource(datalist.get(position).profile_img);

        //프로필 사진
        myImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup();
            }
        });

        ImageView myMap =holder.myMap;
        myMap.setImageResource(datalist.get(position).profile_map);

        //지도
        myMap.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {
                Intent intent = new Intent(context, LocationMap.class);
                context.startActivity(intent);
            }
        });


        TextView nameView =holder.nameView;
        nameView.setText(datalist.get(position).profile_name);

        //이름
        nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "이름 누르면 => 사용자 설정 => 아직 안 함",Toast.LENGTH_SHORT).show();
            }
        });


        TextView msgView =holder.msgView;
        msgView.setText(datalist.get(position).profile_msg);
        //알림명
        msgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "알림명 눌으면 => 알림 변경 => 아직 안 함",Toast.LENGTH_SHORT).show();
            }
        });

    }
        //프로필 클릭 시 전화, 영통, 닫기, 메세지
        public void popup(){
            final Dialog MyDialog = new Dialog(context);

            MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            MyDialog.setContentView(R.layout.activity_click_function);

            Button runCall = (Button)MyDialog.findViewById(R.id.runCall);
            Button videoCall = (Button)MyDialog.findViewById(R.id.videoCall);
            Button close = (Button)MyDialog.findViewById(R.id.btn_back);
            Button msg = (Button)MyDialog.findViewById(R.id.SendMsg);

            //전화
            runCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {                    Toast.makeText(context, "전화",Toast.LENGTH_SHORT).show();


                    Intent intent1 = null;
                    int chk = PermissionChecker.checkSelfPermission(context,
                            Manifest.permission.CALL_PHONE);
                    if(chk==PackageManager.PERMISSION_GRANTED){
                        //권한 승락이 된 상태                           //5554번에서 5556번 전화하기
                        Log.d("tel","성공");
                        intent1 = new Intent(Intent.ACTION_CALL,Uri.parse("tel:010-1234-1234"));

                    }else {
                        Log.d("tel","실패");
                        return;
                    }
                    context.startActivity(intent1);

                }
            });
            //영통
            videoCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "영상통화",Toast.LENGTH_SHORT).show();

                        Intent intent = null;
                        int chk = PermissionChecker.checkSelfPermission(context,
                                Manifest.permission.CALL_PHONE);
                        if(chk== PackageManager.PERMISSION_GRANTED){
                            intent = new Intent(Intent.ACTION_CALL,
                                    Uri.parse("tel:010-1234-1234"));
                            intent.putExtra("andorid.phone.extra.calltype",0);
                            intent.putExtra("videocall",true);
                        }else{
                            Log.d("tel","실패");
                            return;
                        }
                    context.startActivity(intent);


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



    @Override
    public int getItemCount() {
        return datalist.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView myImg;
        ImageView myMap;
        TextView nameView;
        TextView msgView;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.myImg = (ImageView)itemView.findViewById(R.id.profile_img);
            this.myMap = (ImageView)itemView.findViewById(R.id.profile_map);
            this.nameView = (TextView)itemView.findViewById(R.id.profile_name);
            this.msgView = (TextView)itemView.findViewById(R.id.profile_msg);

        }
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



}