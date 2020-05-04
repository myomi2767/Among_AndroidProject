package com.example.among.chatting;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.among.R;
import com.example.among.chatting.model.Chat;
import com.example.among.chatting.model.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatHolder> {

    private ArrayList<Chat> ChatList;

    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd\naa hh:mm");
    //객체 생성되었을 때 ArrayList 생성
    ChattingFragment chattingFragment = new ChattingFragment();
    public ChatListAdapter() {
       ChatList = new ArrayList<>();
   }
    public void setFragment(ChattingFragment chattingFragment){
        this.chattingFragment = chattingFragment;
    }
    public void addItem(Chat chat){
        ChatList.add(chat); //객체 추가
        notifyDataSetChanged();
    }
    public void removeItem(Chat chat){
        int position = getItemPosition(chat.getChatId());
        if ( position > -1 ) {
            ChatList.remove(position);
            notifyDataSetChanged();
        }
    }
    public void updateItem(Chat chat){
        int changedItemPosition = getItemPosition(chat.getChatId());
        if ( changedItemPosition > -1 ) {
            ChatList.set(changedItemPosition ,chat);
            notifyItemChanged(changedItemPosition);
        }
    }
    private int getItemPosition(String chatId) {
        int position = 0;
        for ( Chat currItem : ChatList ) {
            if ( currItem.getChatId().equals(chatId)) {
                return position;
            }
            position++;
        }
        return -1;
    }
    public Chat getItem(int position){
        return this.ChatList.get(position);
    }
    public Chat getItem(String chatId) {
        return getItem(getItemPosition(chatId));
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_chat_item,parent,false);
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        final Chat item = getItem(position);
        Log.d("msg", "item" + item.toString());
        if(item.getLastMessage()!=null){
            if(item.getLastMessage().getMessageType() == Message.MessageType.TEXT){
                holder.lastMessageView.setText(item.getLastMessage().getMessageText());
            }else if (item.getLastMessage().getMessageType() == Message.MessageType.PHOTO){
                holder.lastMessageView.setText("(사진)");
            }else if(item.getLastMessage().getMessageType() == Message.MessageType.EXIT){
                holder.lastMessageView.setText(item.getLastMessage().getMessageUser().getName());
            }

            holder.lastMsgDateView.setText(sdf.format(item.getLastMessage().getMessageDate()));
        }

        holder.titleView.setText(item.getTitle());
        holder.rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(chattingFragment !=null){
                    chattingFragment.leaveChat(item);
                }
                return true;
            }
        });

        if (item.getTotalUnreadCount() > 0) {
            holder.totalUnreadCountView.setText(String.valueOf(item.getTotalUnreadCount())); //int 형 String으로 바꿔주기
        }else{
            holder.totalUnreadCountView.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return ChatList.size();
    }

    public static class ChatHolder extends RecyclerView.ViewHolder{
        CircleImageView ChatThumbView;
        TextView titleView;
        TextView lastMessageView;
        TextView totalUnreadCountView;
        TextView lastMsgDateView;
        LinearLayout rootView;
        public ChatHolder(View v){
            super(v);
            ChatThumbView = v.findViewById(R.id.thumb);
            titleView = v.findViewById(R.id.title);
            lastMessageView = v.findViewById(R.id.lastMessage);
            totalUnreadCountView = v.findViewById(R.id.totalUnreadCount);
            lastMsgDateView = v.findViewById(R.id.lastMsgDate);
            rootView = v.findViewById(R.id.rootView);
        }

    }
}
