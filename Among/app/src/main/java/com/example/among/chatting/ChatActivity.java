package com.example.among.chatting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.among.R;
import com.example.among.chatting.model.Chat;
import com.example.among.chatting.model.Message;
import com.example.among.chatting.model.PhotoMessage;
import com.example.among.chatting.model.TextMessage;
import com.example.among.chatting.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends AppCompatActivity {
    private String mChatId;
    boolean permission_state;
    private static final int TAKE_PHOTO_REQUEST_CODE = 201;
    ImageView sendBtn;
    EditText mMessageText;
    String image_url;
    FirebaseDatabase firebaseDB;
    DatabaseReference chatRef;
    DatabaseReference chatMemberRef;
    DatabaseReference chatMessageRef;
    DatabaseReference mUserRef;
    DatabaseReference myRef;
    StorageReference imageStorageRef;
    FirebaseStorage storage;
    FirebaseUser firebaseUser;
    Toolbar toolbar;
    ImageView photoSend;
    RecyclerView chatRecyclerView;
    MessageListAdapter messageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        /*if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            permission_state = true;
            printToast("권한이 설정되었습니다.");
        }else{
            printToast("권한이 없습니다.");
            //2. 권한이 없는 경우 권한을 설정하라는 메시지를 띄운다.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET,
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            printToast("권한이 설정되었습니다.");
        }*/
        sendBtn = findViewById(R.id.senderBtn);
        mMessageText = findViewById(R.id.edtContent);
        toolbar = findViewById(R.id.toolbar);
        chatRecyclerView = findViewById(R.id.chat_rec_view);
        photoSend = findViewById(R.id.photoSend);
        mChatId = getIntent().getStringExtra("chat_Id");
        firebaseDB = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserRef = firebaseDB.getReference("users");
        //Log.d("msgTestttt","ChatId"+mChatId);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChatId != null) {
                    sendMessage();
                } else {
                    createChat();
                }
            }
        });
        photoSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //안드로이드 파일 창 오픈(갤러리 오픈)
                //TAKE_PHOTO_REQUEST_CODE =201

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //intent.setType("image/*");
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,TAKE_PHOTO_REQUEST_CODE);
            }
        });
        if (mChatId != null) {
            chatRef = firebaseDB.getReference("users").child(firebaseUser.getUid()).child("chats").child(mChatId);
            chatMessageRef = firebaseDB.getReference("chat_messages").child(mChatId);
            chatMemberRef = firebaseDB.getReference("chat_members").child(mChatId);
            ChattingFragment.JOINED_ROOM = mChatId;
            initTotalUnreadCount();
        } else {
            chatRef = firebaseDB.getReference("users").child(firebaseUser.getUid()).child("chats");
        }
        messageListAdapter = new MessageListAdapter();
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(messageListAdapter);


    }
    //액티비티 비활성화 - 리스너가 끊겨도 리스닝을 계속 하고있어야 하기 때문
    @Override
    protected void onPause() {
        super.onPause();
        if (mChatId != null) {
            //onChildadded 호출한 변수의 값 >= 총 메시지
            //포커스 맞추기
            removeMessageListener();
        }
    }
    //액티비티 재개
    @Override
    protected void onResume() {
        super.onResume();
        if (mChatId != null) {
            //총 메세지의 카운터
            chatMessageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long totalMessageCount = dataSnapshot.getChildrenCount();
                    mMessageEventListener.setTotalMessageCount(totalMessageCount);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            messageListAdapter.clearItem();
            addChatListener();
            addMessageListener(); //채팅 아이디가 있을 때만
        }
    }

    private void initTotalUnreadCount() {
        chatRef.child("totalUnreadCount").setValue(0);
    }

    MessageEventListener mMessageEventListener = new MessageEventListener();

    private void addChatListener(){
        //지속적으로 작업할 수 있도록 addValueEventListener
        chatRef.child("title").addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String title = dataSnapshot.getValue(String.class);
                if(title!=null){
                    toolbar.setTitle(title);
                    getSupportActionBar();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void addMessageListener () {
        chatMessageRef.addChildEventListener(mMessageEventListener);
    }

    private void removeMessageListener() {
        chatMessageRef.removeEventListener(mMessageEventListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TAKE_PHOTO_REQUEST_CODE){
            if(data!=null){
                Log.d("myimage", data.getData() + "");
                Log.d("myimage", getPath(data.getData()) + "");
                image_url = getPath(data.getData());
                //File file = new File(image_url);
                upload(getPath(data.getData()));
                //업로드 이미지 처리 - 업로드 이미지 완료된 경우 실제 web에 업로드된 주소를 받아서
                //photo_url로 저장, 메시지 발송
                //uploadImage(data.getData());
                //data : intent 형이기 때문에 uri형인 data.getData() 사용

            }
        }
    }
    private Message.MessageType mMessageType = Message.MessageType.TEXT;
    public void upload(final String uri){
        Log.d("myupload","이미지"+uri);
        StorageReference storageRef = storage.getReference();
        imageStorageRef = FirebaseStorage.getInstance().getReference("/chats/").child(mChatId);

        Uri file = Uri.fromFile(new File(uri));
        StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);
        imageStorageRef.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot task) {

                Log.d("imageUpload","upload::::::::::"+image_url);
                myRef = firebaseDB.getReference("chat_messages").child(mChatId).child("images");
                PhotoMessage photouri = new PhotoMessage();
                photouri.setPhotoUrl(image_url);
                myRef.child("image").push().setValue(photouri);
                mMessageType = Message.MessageType.PHOTO;
                sendMessage();
            }
        });
    }
    public String getPath(Uri uri){
        String[] path = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader =
                new CursorLoader(this,uri,path,null,
                        null,null);
        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(index);
    }



    private class MessageEventListener implements ChildEventListener{

        private long totalMessageCount;
        private long callCount =1;
        //총 갯수 셋팅
        public void setTotalMessageCount(long totalMessageCount) {
            this.totalMessageCount = totalMessageCount;
        }

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            // 신규메세지
            //Log.d("msgTest","리스너 chatMessageRef"+chatMessageRef);
            Message item = dataSnapshot.getValue(Message.class);
            //chat_message>chat-id> message-id>ReadUserList----???
            List<String> readUserIDList = item.getReadUserList();

            if (readUserIDList != null) {
                if (!readUserIDList.contains(firebaseUser.getUid())) {
                    dataSnapshot.getRef().runTransaction(new Transaction.Handler() {


                        @Override
                        public Transaction.Result doTransaction( MutableData mutableData) {
                            Message mutableMessage = mutableData.getValue(Message.class);
                            //readUser에 내 Id추가
                            //UnreadCount --
                            List<String> mutableReadUserList = mutableMessage.getReadUserList();
                            mutableReadUserList.add(firebaseUser.getUid()); // 내가 읽었음을 표시
                            int mutableUnreadCount = mutableMessage.getUnreadCount() - 1;

                            if (mutableMessage.getMessageType() == Message.MessageType.PHOTO) {
                                PhotoMessage mutablePhotoMessage = mutableData.getValue(PhotoMessage.class);
                                mutablePhotoMessage.setReadUserList(mutableReadUserList);
                                mutablePhotoMessage.setUnreadCount(mutableUnreadCount);
                                mutableData.setValue(mutablePhotoMessage);

                            } else {
                                TextMessage mutableTextMessage = mutableData.getValue(TextMessage.class);
                                mutableTextMessage.setReadUserList(mutableReadUserList);
                                mutableTextMessage.setUnreadCount(mutableUnreadCount);
                                mutableData.setValue(mutableTextMessage);
                            }

                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                            //작업이 완료되면 unReadCount 0만들기
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    initTotalUnreadCount();
                                }
                            },500); //0.5초 정도 후 초기화

                        }
                    });
                }

            }
            //ui
            if (item.getMessageType() == Message.MessageType.TEXT) {
                TextMessage textMessage = dataSnapshot.getValue(TextMessage.class);
                messageListAdapter.addItem(textMessage);
            } else if (item.getMessageType() == Message.MessageType.PHOTO) {
                PhotoMessage photoMessage = dataSnapshot.getValue(PhotoMessage.class);
                messageListAdapter.addItem(photoMessage);
            }
            if(callCount >=totalMessageCount){
                //스크롤을 맨 마지막으로 내린다.
                chatRecyclerView.scrollToPosition(messageListAdapter.getItemCount()-1); //index로 가기
            }
            callCount++;
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            //변경된 메시지 - UnreadCount
            //adapter에 변경된 메시지 데이터 전달하고 메시지 id번호로
            // 해당 메시지의 위치를 알아내서
            //알아낸 위치 값을 이용해서 메시지 리스트의 값 변경
            Message item = dataSnapshot.getValue(Message.class);
            if (item.getMessageType() == Message.MessageType.TEXT) {
                TextMessage textMessage = dataSnapshot.getValue(TextMessage.class);
                messageListAdapter.updateItem(textMessage);
            } else if (item.getMessageType() == Message.MessageType.PHOTO) {
                PhotoMessage photoMessage = dataSnapshot.getValue(PhotoMessage.class);
                messageListAdapter.updateItem(photoMessage);
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    }

    Message message = new Message();
    public void sendMessage () {
        //메시지 키 생성
        chatMessageRef = firebaseDB.getReference("chat_messages").child(mChatId);
        //chat_message - {chat-id}-{messageId}-messageInfo
        String messageId = chatMessageRef.push().getKey();
        String messageText = mMessageText.getText().toString();
        //String lastMessage = mMessageText.getText().toString();


        if(mMessageType== Message.MessageType.TEXT){
            if (messageText.isEmpty()) {
                //메시지를 입력하지 않으면 전송이 되지 않도록
                return;
            }
            message = new TextMessage();
            ((TextMessage)message).setMessageText(messageText);

        }else if (mMessageType == Message.MessageType.PHOTO){
            message = new PhotoMessage();
            ((PhotoMessage)message).setPhotoUrl(image_url);

        }
        //textMessage.setMessageText(messageText);
        message.setMessageDate(new Date());
        message.setChatId(mChatId);
        message.setMessageId(messageId);
        message.setMessageType(mMessageType);
        message.setMessageUser(new User(firebaseUser.getUid(), firebaseUser.getEmail(),
                firebaseUser.getDisplayName(), firebaseUser.getPhotoUrl().toString()));
        message.setReadUserList(Arrays.asList(new String[]{firebaseUser.getUid()}));
        String[] uids = getIntent().getExtras().getStringArray("uids"); // 신규 방인 경우에만
        //읽지 않은 사람 수
        if (uids != null) {
            message.setUnreadCount(uids.length - 1);
        }
        mMessageText.setText(""); // 채팅창 공백으로 비우기
        mMessageType = Message.MessageType.TEXT;
        chatMemberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                //unReadCount 셋팅하기 위한 대화 상대의 수 가져오기
                long memberCount = dataSnapshot.getChildrenCount(); //mChatID 갯수
                message.setUnreadCount((int) memberCount - 1);
                chatMessageRef.child(message.getMessageId()).setValue(message, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable final DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Iterator<DataSnapshot> memberIterator = dataSnapshot.getChildren().iterator();
                        while (memberIterator.hasNext()) {
                            User chatMember = memberIterator.next().getValue(User.class); //user 정보 꺼내오기

                            mUserRef
                                    .child(chatMember.getUid())
                                    .child("chats")
                                    .child(mChatId)
                                    .child("lastMessage")
                                    .setValue(message);

                            if (!chatMember.getUid().equals(firebaseUser.getUid())) {
                                mUserRef
                                        .child(chatMember.getUid())
                                        .child("chats")
                                        .child(mChatId)
                                        .child("totalUnreadCount")
                                        .runTransaction(new Transaction.Handler() {
                                            @NonNull
                                            @Override
                                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                                Log.d("test",mutableData+"");
                                                Log.d("test", mutableData.getValue(Long.class)+"");
                                                long totalUnreadCount = mutableData.getValue(long.class) == null ? 0 : mutableData.getValue(long.class);
                                                mutableData.setValue(totalUnreadCount+1);

                                                return Transaction.success(mutableData);
                                            }

                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                                            }
                                        });

                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private boolean isSentMessage = false;
    public void createChat () {
        //방 생성

        final Chat chat = new Chat();
        // 채팅 주소 얻어오기
        mChatId = chatRef.push().getKey();
        chatRef = chatRef.child(mChatId);
        chatMemberRef = firebaseDB.getReference("chat_members").child(mChatId);
        chat.setChatId(mChatId);
        chat.setCreateDate(new Date()); // 생성 날짜
        String uid = getIntent().getStringExtra("uid");
        String[] uids = getIntent().getExtras().getStringArray("uids");
        if (uid != null) {
            //1:1
            uids = new String[]{uid};

        }
        List<String> uidList = new ArrayList<>(Arrays.asList(uids));
        uidList.add(firebaseUser.getUid());
        //채팅방 고유 키 얻어오기

        //사용자 1, 사용자 2 출력...

        for (String userId : uidList) {
            //uid - userInfo
            mUserRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                    User member = dataSnapshot.getValue(User.class);
                    //firebase db에 있는 정보 User 형으로 가져오기
                           /* titleBuffer.append(member.getName());
                            titleBuffer.append(",");*/

                    chatMemberRef.child(member.getUid()).
                            setValue(member, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    //방정보 Users-uid-chats-{chat-id}-chatInfo
                                    dataSnapshot.getRef().child("chats").child(mChatId).setValue(chat);
                                    if (!isSentMessage) {
                                        sendMessage();
                                        addMessageListener();
                                        addChatListener();
                                        isSentMessage = true;

                                        ChattingFragment.JOINED_ROOM =mChatId;
                                    }
                                }
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
}

