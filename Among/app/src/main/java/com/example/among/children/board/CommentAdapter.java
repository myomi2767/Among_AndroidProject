package com.example.among.children.board;


import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.among.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    BoardSelect context;
    ArrayList<CommentItem> data;
    int res_id;
    String userID;
    int itemPos;

    public CommentAdapter(){
        super();
    }

    public CommentAdapter(final BoardSelect context, ArrayList<CommentItem> data, int res_id, String userID) {
        this.context = context;
        this.data = data;
        this.res_id = res_id;
        this.userID = userID;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_comment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final CommentItem items = data.get(position);
        holder.id.setText(items.getUserid());
        holder.comment.setText(items.getComment());
        holder.date.setText(items.getDate());
        itemPos = holder.getAdapterPosition();
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView id;
        TextView comment;
        TextView date;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.comment_id);
            comment = itemView.findViewById(R.id.comment_comment);
            date = itemView.findViewById(R.id.comment_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("position",getAdapterPosition()+"");
                }
            });

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "편집");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
        }

        public final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int adapterPos = getAdapterPosition();
                if (adapterPos == RecyclerView.NO_POSITION) {
                    Log.d("comment","아이템 없음 >> NO_POSTION"+adapterPos);
                }else{
                    Log.d("comment","아이템 있음 >> Not NO_POSTION"+adapterPos);
                }
                int currentPos = adapterPos;

                CommentItem cItem = data.get(currentPos);
                Log.d("comment","getNum() ::: "+cItem.getNum());
                Log.d("comment","getComment() ::: "+cItem.getComment());
                Log.d("comment","getUserid() ::: "+cItem.getUserid());

                switch (item.getItemId()){
                    case 1001 : // 편집 항목 선택시
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View view = LayoutInflater.from(context).inflate(R.layout.board_comment_update, null, false);
                        builder.setView(view);
                        final Button SubmitButton = (Button) view.findViewById(R.id.button_comment_update);
                        final EditText editTextTitle = (EditText) view.findViewById(R.id.comment_update);
                        final TextView TextViewSeq = view.findViewById(R.id.editView_cSeq);

                        editTextTitle.setText(data.get(currentPos).getComment());
                        TextViewSeq.setText(data.get(currentPos).getNum()+"");

                        final AlertDialog dialog = builder.create();
                        SubmitButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int seq = Integer.parseInt(TextViewSeq.getText().toString());
                                String comment = editTextTitle.getText().toString();

                                CommentItem item = new CommentItem(seq, comment, userID);

                                HttpUpdate task = new HttpUpdate();
                                task.execute(item);

                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                    case 1002:
                        CommentItem mItem = data.get(currentPos);
                        int seq = mItem.getNum();
                        Log.d("comment","seq:"+seq);
                        Log.d("comment","userID:"+userID);

                        CommentItem item2 = new CommentItem(seq, userID);
                        HttpDelete task = new HttpDelete();
                        task.execute(item2);

                        notifyItemChanged(currentPos);
                        CommentAdapter.this.notifyDataSetChanged();
                        notifyItemRangeChanged(currentPos, data.size());
                        Log.d("comment",currentPos+"");
                        break;
                }
                return true;
            }
        };
    }

    class HttpUpdate extends AsyncTask<CommentItem, Void, String> {
        URL url = null;
        BufferedReader br = null;
        String data = "";
        @Override
        protected String doInBackground(CommentItem... items) {
            URL url = null;
            JSONObject object = new JSONObject();
            String data = "";
            try {
                object.put("comment_seq", items[0].getNum());
                object.put("mcomment", items[0].getComment());
                object.put("user_id",items[0].getUserid());
                url = new URL("http://192.168.219.106:8088/among/board/comment/update");

                OkHttpClient client = new OkHttpClient();
                String json = object.toString();
                Log.d("msg", json);
                Request request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(MediaType.parse("application/json"), json))
                        .build();

                Response response = client.newCall(request).execute();
                data = response.body().string();
                Log.d("msg", data);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("true")) {
                Toast.makeText(context.getBaseContext(), "댓글이 수정되었습니다.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context.getBaseContext(), "다시 시도해주세요", Toast.LENGTH_LONG).show();
            }
        }
    }

    class HttpDelete extends AsyncTask<CommentItem, Void, String> {
        URL url = null;
        BufferedReader br = null;
        String data = "";
        @Override
        protected String doInBackground(CommentItem... items) {
            URL url = null;
            JSONObject object = new JSONObject();
            String data = "";
            try {
                object.put("comment_seq", items[0].getNum());
                object.put("user_id",items[0].getUserid());
                url = new URL("http://192.168.219.106:8088/among/board/comment/delete");

                OkHttpClient client = new OkHttpClient();
                String json = object.toString();
                Log.d("msg", json);
                Request request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(MediaType.parse("application/json"), json))
                        .build();

                Response response = client.newCall(request).execute();
                data = response.body().string();
                Log.d("msg", data);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("true")) {
                Toast.makeText(context.getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context.getApplicationContext(), "다시 시도해주세요", Toast.LENGTH_LONG).show();
            }
        }
    }
}
