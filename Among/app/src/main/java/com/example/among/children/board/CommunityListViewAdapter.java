package com.example.among.children.board;


import android.app.AlertDialog;
import android.content.Intent;
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
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.among.R;
import com.example.among.children.fragment.communityFragment;

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

public class CommunityListViewAdapter extends RecyclerView.Adapter<CommunityListViewAdapter.ViewHolder>{
    communityFragment context;
    ArrayList<CommunityListViewItem> data;
    ArrayList<CommunityListViewItem> filterData;
    int res_id;
    String userID;

    public CommunityListViewAdapter(){
        super();
    }


    public CommunityListViewAdapter(communityFragment context, final ArrayList<CommunityListViewItem> data, int res_id, String userID) {
        this.context = context;
        this.data = data;
        this.res_id = res_id;
        this.userID = userID;
        filterData = new ArrayList<>(data);
        notifyDataSetChanged();
    }

    public CommunityListViewAdapter(communityFragment context, ArrayList<CommunityListViewItem> data,
                                    ArrayList<CommunityListViewAdapter> filerData, String userID) {

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_community_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final CommunityListViewItem items = filterData.get(position);
        holder.seq.setText(items.getSeq()+"");
        holder.title.setText(items.getTitle());
        holder.text.setText(items.getText());
    }

    @Override
    public int getItemCount() {
        return filterData.size();
    }

    public Filter getFilter() { // 내용으로 검색
        Toast.makeText(context.getContext(), "내용으로 검색 실행", Toast.LENGTH_LONG).show();
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()) {
                    filterData = data;
                } else {
                    ArrayList<CommunityListViewItem> filteringList = new ArrayList<>();

                    for(CommunityListViewItem item : data) {
                        String name = item.getText();
                        if(name.toLowerCase().contains(charString.toLowerCase())) {
                            filteringList.add(item);
                        }
                    }
                    filterData = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filterData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filterData = (ArrayList<CommunityListViewItem>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    public Filter getFilter2() { // 제목으로 검색
        Toast.makeText(context.getContext(), "제목으로 검색 실행", Toast.LENGTH_LONG).show();
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()) {
                    filterData = data;
                } else {
                    ArrayList<CommunityListViewItem> filteringList = new ArrayList<>();

                    for(CommunityListViewItem item : data) {
                        String name = item.getTitle();
                        if(name.toLowerCase().contains(charString.toLowerCase())) {
                            filteringList.add(item);
                        }
                    }
                    filterData = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filterData;
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filterData = (ArrayList<CommunityListViewItem>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView seq;
        TextView title;
        TextView text;
        final RecyclerView listview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            seq = (TextView) itemView.findViewById(R.id.board_seq);
            title = (TextView)itemView.findViewById(R.id.board_title);
            text = (TextView)itemView.findViewById(R.id.board_text);
            listview = (RecyclerView)itemView.findViewById(R.id.recycler_listview);

            itemView.setOnClickListener(new View.OnClickListener() {
                CommunityListViewItem cItem;
                @Override
                public void onClick(View v) {
                    cItem = data.get(getAdapterPosition());
                    Intent intent = new Intent(context.getContext(), BoardSelect.class);
                    intent.putExtra("board_seq",cItem.getSeq());
                    Log.d("msg","CLVAdapter="+cItem.getSeq());
                    intent.putExtra("title",cItem.getTitle());
                    intent.putExtra("text",cItem.getText());
                    intent.putExtra("time",cItem.getDate());
                    intent.putExtra("userID",userID);
                    context.startActivity(intent);
                }
            });

            itemView.setOnCreateContextMenuListener(this);
        }


        // 3. 컨텍스트 메뉴를 생성하고 메뉴 항목 선택시 호출되는 리스너를 등록해줍니다. ID 1001, 1002로 어떤 메뉴를 선택했는지 리스너에서 구분하게 됩니다.
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
                switch (item.getItemId()){
                    case 1001 : // 편집 항목 선택시
                        AlertDialog.Builder builder = new AlertDialog.Builder(context.getContext());
                        View view = LayoutInflater.from(context.getContext()).inflate(R.layout.board_edit_box, null, false);
                        builder.setView(view);
                        final Button SubmitButton = (Button) view.findViewById(R.id.button_edit_submit);
                        final TextView editTextSeq = (TextView) view.findViewById(R.id.editView_seq);
                        final EditText editTextTitle = (EditText) view.findViewById(R.id.editView_title);
                        final EditText editTextText = (EditText) view.findViewById(R.id.editView_text);

                        editTextSeq.setText(data.get(getAdapterPosition()).getSeq()+"");
                        editTextTitle.setText(data.get(getAdapterPosition()).getTitle());
                        editTextText.setText(data.get(getAdapterPosition()).getText());

                        final AlertDialog dialog = builder.create();
                        SubmitButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int seq = Integer.parseInt(editTextSeq.getText().toString());
                                String title = editTextTitle.getText().toString();
                                String text = editTextText.getText().toString();

                                CommunityListViewItem item = new CommunityListViewItem(seq, title, text, userID);

                                HttpUpdate task = new HttpUpdate();
                                task.execute(item);

                                Log.d("msg",item.getText());

                                //data.set(getAdapterPosition(), item);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                    case 1002:
                        int seq = data.get(getAdapterPosition()).getSeq();

                        CommunityListViewItem item2 = new CommunityListViewItem(seq, userID);
                        HttpDelete task = new HttpDelete();
                        task.execute(item2);

                        //data.remove(getAdapterPosition());
                        notifyItemChanged(getAdapterPosition());
                        CommunityListViewAdapter.this.notifyDataSetChanged();
                        notifyItemRangeChanged(getAdapterPosition(), data.size());
                        break;
                }
                return true;
            }
        };
    }

    class HttpUpdate extends AsyncTask<CommunityListViewItem, Void, String> {
        URL url = null;
        BufferedReader br = null;
        String data = "";
        @Override
        protected String doInBackground(CommunityListViewItem... items) {
            URL url = null;
            JSONObject object = new JSONObject();
            String data = "";
            try {
                object.put("board_seq", items[0].getSeq());
                object.put("title", items[0].getTitle());
                object.put("text", items[0].getText());
                object.put("user_id",items[0].getUserid());
                url = new URL("http://192.168.219.106:8088/among/board/update");

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
                Toast.makeText(context.getContext(), "수정되었습니다.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context.getContext(), "다시 시도해주세요", Toast.LENGTH_LONG).show();
            }
        }
    }

    class HttpDelete extends AsyncTask<CommunityListViewItem, Void, String> {
        URL url = null;
        BufferedReader br = null;
        String data = "";
        @Override
        protected String doInBackground(CommunityListViewItem... items) {
            URL url = null;
            JSONObject object = new JSONObject();
            String data = "";
            try {
                object.put("board_seq", items[0].getSeq());
                object.put("user_id",items[0].getUserid());
                url = new URL("http://192.168.219.106:8088/among/board/delete");

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
                Toast.makeText(context.getContext(), "삭제되었습니다.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context.getContext(), "다시 시도해주세요", Toast.LENGTH_LONG).show();
            }
        }
    }
}


