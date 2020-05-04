package com.example.among.chatting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.among.R;
import com.example.among.chatting.model.Chat;
import com.example.among.chatting.model.ExitMessage;
import com.example.among.chatting.model.Message;
import com.example.among.chatting.model.Notification;
import com.example.among.chatting.model.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Iterator;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChattingFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDB;
    DatabaseReference mChatRef;
    DatabaseReference mChatMemberRef;
    DatabaseReference mChatMessageRef;
    RecyclerView chatRecyclerView;
    ChatListAdapter chatListAdapter;
    Notification notification;
    Context mContext;
    public static final int JOIN_ROOM_REQUEST_CODE = 100;
    public static String JOINED_ROOM = "";

    public ChattingFragment() {
    }
    public static ChattingFragment newInstance(String param1, String param2) {
        ChattingFragment fragment = new ChattingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chatting, container, false);
        chatRecyclerView = view.findViewById(R.id.ChatRecyclerView);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDB = FirebaseDatabase.getInstance();
        mChatRef = firebaseDB.getReference("users").child(firebaseUser.getUid()).child("chats");
        mChatMemberRef = firebaseDB.getReference("chat_members");
        mChatMessageRef = firebaseDB.getReference("chat_messages");


        chatListAdapter = new ChatListAdapter();
        //chatListAdapter = new ChatListAdapter(getActivity(),R.layout.fragment_chat_item,new ArrayList<Chat>());
        chatListAdapter.setFragment(this);//현재 프래그먼트는 chat이기 때문에
        LinearLayoutManager manager = new LinearLayoutManager(getContext());

        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setAdapter(chatListAdapter);
        chatRecyclerView.setLayoutManager(manager);

        chatRecyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getContext(), new RecyclerViewItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Chat chat = chatListAdapter.getItem(position);
                Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
                chatIntent.putExtra("chat_Id", chat.getChatId());
                startActivityForResult(chatIntent,JOIN_ROOM_REQUEST_CODE );

            }
        }));
        mContext = getActivity();
        notification = new Notification(mContext);//노티 객체 생성
        addChatListener();
        return view;
    }


    private void addChatListener() {
        mChatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot chatDataSnapshot, String s) {
                // ui 갱신 시켜주는 메서드로 방의 정보를 전달.
                drawUI(chatDataSnapshot, DrawType.ADD);
            }

            @Override
            public void onChildChanged(final DataSnapshot chatDataSnapshot, String s) {
                // 나의 내가 보낸 메시지가 아닌경우와 마지막 메세지가 수정이 되었다면 -> 노티출력
                //onChildChanged --TotalUnreadCount의 변경. title의 변경, lastMessage변경 시 호출
                drawUI(chatDataSnapshot, DrawType.UPDATE);
                final Chat updatedChat = chatDataSnapshot.getValue(Chat.class);

                if (updatedChat.getLastMessage() != null ) {
                    if(updatedChat.getLastMessage().getMessageType()== Message.MessageType.EXIT){
                        return;
                    }
                    if ( !updatedChat.getLastMessage().getMessageUser().getUid().equals(firebaseUser.getUid())) {
                        // 현재 그 방에 들어가 있지 않을 때
                        if ( !updatedChat.getChatId().equals(JOINED_ROOM)) {
                            // 노티피케이션 알림
                            Intent chatIntent = new Intent(mContext, ChatActivity.class);
                            chatIntent.putExtra("chat_Id", updatedChat.getChatId());
                            notification
                                    .setData(chatIntent)
                                    .setTitle(updatedChat.getLastMessage().getMessageUser().getName())
                                    .setText(updatedChat.getLastMessage().getMessageText())
                                    .notification();

                        }
                    }
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //방의 실시간 삭제
                Chat item = dataSnapshot.getValue(Chat.class);
                chatListAdapter.removeItem(item);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void drawUI(final DataSnapshot chatDataSnapshot, final DrawType drawType){

        final Chat chatRoom = chatDataSnapshot.getValue(Chat.class);
        Log.d("test",chatRoom+":::챗룸");
        //Log.d("test",mChatMemberRef+":::챗룸");
        mChatMemberRef.child(chatRoom.getChatId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long memberCount = dataSnapshot.getChildrenCount();
                Iterator<DataSnapshot> memberIterator = dataSnapshot.getChildren().iterator();
                StringBuffer memberStringBuffer = new StringBuffer();


                if ( memberCount <= 1 ) {
                    chatRoom.setTitle("대화상대가 없는 방입니다.");
                    chatDataSnapshot.getRef().child("title").setValue(chatRoom.getTitle());
                    chatDataSnapshot.getRef().child("disabled").setValue(true);
                    if ( drawType == DrawType.ADD) {
                        chatListAdapter.addItem(chatRoom);
                    } else {
                        chatListAdapter.updateItem(chatRoom);
                    }
                    return;
                }

                int loopCount = 1;
                while( memberIterator.hasNext()) {
                    User member = memberIterator.next().getValue(User.class);
                    if ( !firebaseUser.getUid().equals(member.getUid())) {
                        memberStringBuffer.append(member.getName());
                        if ( memberCount - loopCount > 1 ) {
                            memberStringBuffer.append(", ");
                        }
                    }
                    if ( loopCount == memberCount ) {
                        // users/uid/chats/{chat_id}/title
                        String title = memberStringBuffer.toString();
                        if ( chatRoom.getTitle() == null ) {
                            chatDataSnapshot.getRef().child("title").setValue(title);
                        } else if (!chatRoom.getTitle().equals(title)){
                            chatDataSnapshot.getRef().child("title").setValue(title);
                        }
                        chatRoom.setTitle(title);
                        if ( drawType == DrawType.ADD) {
                            chatListAdapter.addItem(chatRoom);
                        } else {
                            chatListAdapter.updateItem(chatRoom);
                        }
                    }
                    loopCount++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void leaveChat(final Chat chat) {
        final DatabaseReference messageRef = firebaseDB.getReference("chat_messages").child(chat.getChatId());

        Snackbar.make(getView(), "선택된 대화방을 나가시겠습니까?", Snackbar.LENGTH_LONG).setAction("예", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 나의 대화방 목록에서 제거
                // users/{uid}/chats
                mChatRef.child(chat.getChatId()).removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(final DatabaseError databaseError, DatabaseReference chatRef) {
                        // 채팅 멤버 목록에서 제거
                        // chat_members/{chat_id}/{user_id} 제거
                        //chat_messages - chat_id - message_id >> exit 메시지 발송
                        final ExitMessage exitMessage = new ExitMessage();
                        String messageId = messageRef.push().getKey();

                        exitMessage.setMessageUser(new User(firebaseUser.getUid(),firebaseUser.getEmail(),firebaseUser.getDisplayName(),firebaseUser.getPhotoUrl().toString()));
                        exitMessage.setMessageDate(new Date());
                        exitMessage.setMessageId(messageId);
                        exitMessage.setChatId(chat.getChatId());
                        messageRef.child(messageId).setValue(exitMessage); //방 나감 메시지 발송

                        //채팅방의 멤버 정보 가져와서 채팅방의 정보 가져옴


                       mChatMemberRef
                               .child(chat.getChatId())
                               .child(firebaseUser.getUid())
                               .removeValue(new DatabaseReference.CompletionListener(){

                                   @Override
                                   public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                       mChatMemberRef
                                               .child(chat.getChatId())
                                               .addListenerForSingleValueEvent(new ValueEventListener() {
                                                   @Override
                                                   public void onDataChange(DataSnapshot dataSnapshot) {
                                                       Iterator<DataSnapshot> chatMemberIterator = dataSnapshot.getChildren().iterator();

                                                       while ( chatMemberIterator.hasNext()) {
                                                           User chatMember = chatMemberIterator.next().getValue(User.class);

                                                           // chats -> {uid} -> {chat_id} - { ... }
                                                           firebaseDB
                                                                   .getReference("users")
                                                                   .child(chatMember.getUid())
                                                                   .child("chats")
                                                                   .child(chat.getChatId())
                                                                   .child("lastMessage")
                                                                   .setValue(exitMessage);
                                                       }
                                                   }

                                                   @Override
                                                   public void onCancelled(DatabaseError databaseError) {

                                                   }
                                               });
                                                   firebaseDB.getReference("messages").child(chat.getChatId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                       @Override
                                                       public void onDataChange(DataSnapshot dataSnapshot) {
                                                           Iterator<DataSnapshot> messageIterator = dataSnapshot.getChildren().iterator();

                                                           while ( messageIterator.hasNext()) {
                                                               DataSnapshot messageSnapshot = messageIterator.next();
                                                               Message currentMessage = messageSnapshot.getValue(Message.class);
                                                               if ( !currentMessage.getReadUserList().contains(firebaseUser.getUid())) {
                                                                   // message
                                                                   messageSnapshot.child("unreadCount").getRef().setValue(currentMessage.getUnreadCount() - 1);
                                                               }
                                                           }
                                                       }

                                                   @Override
                                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                                   }
                                               });
                                           }
                                       });
                                       mChatMemberRef.child(chat.getChatId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(DataSnapshot dataSnapshot) {
                                               Iterator<DataSnapshot> memberIterator = dataSnapshot.getChildren().iterator();

                                               while( memberIterator.hasNext()) {
                                                   // 방 참여자의 UID를 가져오기 위하여 user 정보 조회
                                                   User chatMember = memberIterator.next().getValue(User.class);
                                                   // 해당 참여자의 방 정보의 업데이트를 위하여 방이름을 임의로 업데이트 진행
                                                   firebaseDB.getReference("users")
                                                           .child(chatMember.getUid())
                                                           .child("chats")
                                                           .child(chat.getChatId())
                                                           .child("title")
                                                           .setValue("");
                                               }
                                           }

                                           @Override
                                           public void onCancelled(DatabaseError databaseError) {

                                           }
                                       });
                                   }
                               });
                            }
                        }).show();
                    }

            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if ( requestCode == JOIN_ROOM_REQUEST_CODE ) {
                    JOINED_ROOM = "";
                }
            }

            private enum DrawType  {
                ADD, UPDATE
            }
        }