package com.example.among.children.user;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.among.R;

public class ViewHolder {
    ImageView myImg;
    ImageView myMap;
    TextView nameView;
    TextView msgView;

    public ViewHolder(View parentView) {
        this.myImg = parentView.findViewById(R.id.profile_img);
        this.myMap = parentView.findViewById(R.id.profile_map);
        this.nameView = parentView.findViewById(R.id.profile_name);
        this.msgView = parentView.findViewById(R.id.profile_msg);

    }
}
