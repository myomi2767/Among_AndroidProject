package com.example.among.chatting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.among.R;
import com.example.among.chatting.model.Message;
import com.example.among.chatting.model.PhotoMessage;
import com.example.among.chatting.model.TextMessage;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MessageViewHolder>{
    ArrayList<Message> mMessageList;
    private String UserId;
    private SimpleDateFormat messageDateFormat = new SimpleDateFormat("MM/dd a\n hh:mm");
    public MessageListAdapter(){
        mMessageList = new ArrayList<>();
        UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    public void addItem(Message item){
        mMessageList.add(item);
        notifyDataSetChanged();
    }
    public void updateItem(Message item){
        int position = getItemPosition(item.getMessageId());
        if (position<0){
            return;
        }
        mMessageList.set(position,item);
        notifyItemChanged(position);

    }
    public void clearItem(){
        mMessageList.clear();
    }
    public int getItemPosition(String messageId){
        int position = 0;
        for(Message message :mMessageList){
            if(message.getMessageId().equals(messageId)){
                return position;
            }
            position++;
        }
        return -1;
    }

    public Message getItem(int position){
        return mMessageList.get(position);
    }
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //View를 이용한 ViewHolder 리턴
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_message_item,parent,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        //전달받은 뷰홀더를 이용한 뷰 구현
        Message item = getItem(position);

        TextMessage textMessage = null;
        PhotoMessage photoMessage = null;

        if (item instanceof TextMessage){
            textMessage = (TextMessage)item;
        }else if (item instanceof PhotoMessage){
            photoMessage = (PhotoMessage)item;
        }

        //내가 보낸 메시지인지 받은 메시지인지 판별 필요
        if (UserId.equals(item.getMessageUser().getUid())){
            //내가 보낸 메시지 출력
            //텍스트 메시지인지 포토 메시지인지 구별 필요!
            if (item.getMessageType() == Message.MessageType.TEXT){
                holder.sendTxt.setText(textMessage.getMessageText());
                holder.sendTxt.setVisibility(View.VISIBLE);
                holder.sendImage.setVisibility(View.GONE);
            }else if(item.getMessageType() == Message.MessageType.PHOTO){
                Glide.with(holder.sendArea).load(photoMessage.getPhotoUrl())
                        .into(holder.sendImage);
                holder.sendTxt.setVisibility(View.GONE);
                holder.sendImage.setVisibility(View.VISIBLE);
            }
            if (item.getUnreadCount()>0){
                holder.sendUnreadCount.setText(String.valueOf(item.getUnreadCount()));
            }else{
                holder.sendUnreadCount.setText("");
            }

            holder.sendDate.setText(messageDateFormat.format(item.getMessageDate()));
            holder.yourChatArea.setVisibility(View.GONE);
            holder.sendArea.setVisibility(View.VISIBLE);
            holder.exitArea.setVisibility(View.GONE);
        }else{
            //상대방이 보낸 경우
            if (item.getMessageType() == Message.MessageType.TEXT){
                holder.rcvTxtView.setText(textMessage.getMessageText());
                holder.rcvTxtView.setVisibility(View.VISIBLE);
                holder.rcvImage.setVisibility(View.GONE);
            }else if(item.getMessageType() == Message.MessageType.PHOTO){
                Glide
                        .with(holder.yourChatArea)
                        .load(photoMessage.getPhotoUrl())
                        .into(holder.rcvImage);

                holder.rcvTxtView.setVisibility(View.GONE);
                holder.rcvImage.setVisibility(View.VISIBLE);
            }else if(item.getMessageType() == Message.MessageType.EXIT){
                //{이름}님이 나가셨습니다.
                holder.exitText.setText(item.getMessageUser().getName()+"님이 방에서 나가셨습니다.");

            }
            if(item.getUnreadCount()>0){
                holder.rcvUnreadCount.setText(String.valueOf(item.getUnreadCount()));
            }else{ //item.getUnreadCount() = 0일때는 칸을 비워준다.
                holder.sendUnreadCount.setText("");
            }
            if(item.getMessageUser().getProfileUrl()!=null){
                Glide.with(holder.yourChatArea)
                        .load(item.getMessageUser().getProfileUrl())
                        .into(holder.rcvProfileView);
            }
            if(item.getMessageType()==Message.MessageType.EXIT){
                holder.yourChatArea.setVisibility(View.GONE);
                holder.sendArea.setVisibility(View.GONE);
                holder.exitArea.setVisibility(View.VISIBLE);
            }else{
                holder.rcvDate.setText(messageDateFormat.format(item.getMessageDate()));
                holder.yourChatArea.setVisibility(View.VISIBLE);
                holder.sendArea.setVisibility(View.GONE);

            }
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        LinearLayout yourChatArea; //상대방
        LinearLayout sendArea;
        LinearLayout exitArea;
        CircleImageView rcvProfileView;
        TextView rcvTxtView; //상대방
        ImageView rcvImage;
        TextView rcvUnreadCount;
        TextView rcvDate; //수신 날짜
        TextView sendUnreadCount;
        TextView sendDate;
        TextView sendTxt;
        TextView exitText;
        ImageView sendImage;

     public MessageViewHolder(View view){
         super(view);
         yourChatArea = view.findViewById(R.id.yourChatArea);
         sendArea = view.findViewById(R.id.myChatArea);
         exitArea = view.findViewById(R.id.exitArea);
         rcvProfileView = view.findViewById(R.id.rcvProfile);
         rcvTxtView = view.findViewById(R.id.rcvTxt);
         rcvImage  = view.findViewById(R.id.rcvImage);
         rcvUnreadCount = view.findViewById(R.id.rcvUnreadCount);
         rcvDate = view.findViewById(R.id.rcvDate);
         sendUnreadCount = view.findViewById(R.id.sendUnreadCount);
         sendDate = view.findViewById(R.id.sendDate);
         sendTxt = view.findViewById(R.id.sendTxt);
         exitText = view.findViewById(R.id.exitTxt);
         sendImage = view.findViewById(R.id.sendImage);
     }

 }
}
