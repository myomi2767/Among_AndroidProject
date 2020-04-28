package com.example.among.children.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileAdapter extends ArrayAdapter<ProfileItem> {
    private Context context;
    private int resId;
    private ArrayList<ProfileItem> datalist;

    HashMap<Integer, UserState> saveData = new HashMap<Integer, UserState>();

    public ProfileAdapter(Context context, int resId, ArrayList<ProfileItem> datalist) {
        super(context, resId, datalist);
        this.context = context;
        this.resId = resId;
        this.datalist = datalist;
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public ProfileItem getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resId, null);

            //뷰를 만드는 최초 작업 찾아서 가져오기
            holder = new ViewHolder(convertView);
            //홈더를 저장
            convertView.setTag(holder);
        }else {//여기는 뷰를 재 사용중
            holder = (ViewHolder)convertView.getTag();
        }

        //ArrayList에서 리턴된 리스트 항목의 번호와 동일한 데이터를 구하기
        ProfileItem user= datalist.get(position);
        if(user!=null){

            ImageView imageView = holder.myImg;
            ImageView mapView = holder.myMap;
            TextView nameView = holder.nameView;
            TextView msgView = holder.msgView;


            imageView.setImageResource(user.profile_img);
            mapView.setImageResource(user.profile_map);
            nameView.setText(user.profile_name);
            msgView.setText(user.profile_msg);

        }

        return convertView;
    }
}