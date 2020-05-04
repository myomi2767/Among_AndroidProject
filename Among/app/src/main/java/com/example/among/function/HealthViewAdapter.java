package com.example.among.function;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.among.R;

import java.util.ArrayList;


public class HealthViewAdapter
        extends RecyclerView.Adapter<HealthViewAdapter.ViewHolder>{
    HealthFragment context;
    ArrayList<NurseViewItem> data;
    int res_id;

    public HealthViewAdapter(HealthFragment context, int res_id, ArrayList<NurseViewItem> data) {
        this.context = context;
        this.data = data;
        this.res_id=res_id;
    }

    //클릭 이벤트를 위한 메소드 작성 ---------------------------------------
    public interface MyOnItemClickListener{
        void onItemClick(View v, int pos);
    }

    private HealthViewAdapter.MyOnItemClickListener myListener = null;

    public void setOnItemClickListener(HealthViewAdapter.MyOnItemClickListener listener){
        this.myListener = listener;
    }
    //------------------------------------------------------------------

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final NurseViewItem items = data.get(position);
        holder.name.setText(items.getName());

    }


    @Override
    public int getItemCount() {
        return data.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        NurseViewItem item = data.get(pos);
                        if (myListener != null){
                            myListener.onItemClick(v, pos);
                        }
                    }
                }
            });
        }
    }
}
