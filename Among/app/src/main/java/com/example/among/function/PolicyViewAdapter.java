package com.example.among.function;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.among.R;

import java.util.ArrayList;


public class PolicyViewAdapter
        extends RecyclerView.Adapter<PolicyViewAdapter.ViewHolder>{
    PolicyFragment context;
    ArrayList<PolicyViewItem> data;
    int res_id;

    public PolicyViewAdapter(PolicyFragment context, int res_id, ArrayList<PolicyViewItem> data) {
        this.context = context;
        this.data = data;
        this.res_id=res_id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PolicyViewItem items = data.get(position);
        holder.image.setImageResource(items.getImage());
        holder.name.setText(items.getName());
        holder.pre.setText(items.getPre());

    }


    @Override
    public int getItemCount() {
        return data.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        TextView pre;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            pre = itemView.findViewById(R.id.pre);
        }
    }
}
