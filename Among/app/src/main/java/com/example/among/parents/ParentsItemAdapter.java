package com.example.among.parents;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.among.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParentsItemAdapter extends RecyclerView.Adapter<ParentsItemAdapter.ViewHolder> {
    Parents context;
    int row_res_id; // row를 구성하는 layout
    List<ParentsItem> data; //RecyclerView에 출력될 전체 데이터

    public ParentsItemAdapter(Parents context, int row_res_id, List<ParentsItem> data) {
        this.context = context;
        this.row_res_id = row_res_id;
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.parents_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CircleImageView img = holder.img;
        final TextView txt_view = holder.txtview;

        txt_view.setText(data.get(position).getName());
        img.setImageResource(data.get(position).getImg());
        final String number = data.get(position).getPhone_number();
        Log.d("mytest",number);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getBaseContext(), Parents02.class);
                intent.putExtra("name", data.get(position).getName());
                intent.putExtra("number", number);
                intent.putExtra("img",data.get(position).getImg());
                context.startActivity(intent);
                Toast.makeText(context, position+"번째 아이템 클릭", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtview;
        CircleImageView img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtview = itemView.findViewById(R.id.childName);
            img = itemView.findViewById(R.id.parentsitemview);
        }

    }

}