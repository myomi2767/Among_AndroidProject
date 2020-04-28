package com.example.among.children.user;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.among.R;

import java.util.List;

public class ProfileAdapter
        extends RecyclerView.Adapter<ProfileAdapter.CustomViewHolder> {

    private Context context;
    private int resId;
    private List<ProfileItem> datalist;

   /* HashMap<Integer, UserState> saveData = new HashMap<Integer, UserState>();
    ViewHolder viewHolder = new ViewHolder();*/

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

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        ImageView myImg =holder.myImg;
        myImg.setImageResource(datalist.get(position).profile_img);
        myImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup();
            }
        });

        ImageView myMap =holder.myMap;
        myMap.setImageResource(datalist.get(position).profile_map);
        myMap.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {
                popupMap();
            }
        });


        TextView nameView =holder.nameView;
        nameView.setText(datalist.get(position).profile_name);
        nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup();
            }
        });


        TextView msgView =holder.msgView;
        msgView.setText(datalist.get(position).profile_msg);
        msgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup();
            }
        });

/*      holder.myImg.setImageResource(datalist.get(position).profile_img);
        holder.myMap.setImageResource(datalist.get(position).profile_map);
        holder.nameView.setText(datalist.get(position).profile_name);
        holder.msgView.setText(datalist.get(position).profile_msg);

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String crrntName = holder.profile_name.getText().toString(); 이거 대신 아래 text
                Toast.makeText(v.getContext(), "짧게 클릭하셨습니다.", Toast.LENGTH_SHORT).show();
                popup();


            }
        });*/
    }

        public void popup(){
            final Dialog MyDialog = new Dialog(context);
            MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            MyDialog.setContentView(R.layout.activity_click_function);

            Button close = (Button)MyDialog.findViewById(R.id.btn_back);
            Button runCall = (Button)MyDialog.findViewById(R.id.run_call);

            runCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "전화",Toast.LENGTH_SHORT).show();

                }
            });
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyDialog.cancel();
                }
            });
            MyDialog.show();
        }
    public void popupMap(){
        final Dialog MyDialog = new Dialog(context);
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.activity_click_function);

        Button close = (Button)MyDialog.findViewById(R.id.btn_back);
        Button runCall = (Button)MyDialog.findViewById(R.id.run_call);

        runCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "전화",Toast.LENGTH_SHORT).show();

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.cancel();
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
}