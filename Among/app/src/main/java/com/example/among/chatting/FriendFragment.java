package com.example.among.chatting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.among.R;
import com.example.among.chatting.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FloatingActionButton friendFab;
    LinearLayout searchArea ;
    EditText editEmail;
    ImageButton findBtn;
    RecyclerView recyclerView;
    FriendFragment friendFragment;
    Toolbar toolbar;
    CheckBox checkBox;
    FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userDBRef;
    private DatabaseReference friendsDBRef;
    private FriendsListAdapter friendsListAdapter;
    public FriendFragment() {
        // Required empty public constructor
    }
    public static FriendFragment newInstance(String param1, String param2) {
        FriendFragment fragment = new FriendFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View viewGroup = (View)LayoutInflater.from(getContext()).inflate(R.layout.fragment_friend, container, false);
        searchArea = viewGroup.findViewById(R.id.search_area);
        friendFab = viewGroup.findViewById(R.id.friendFab);
        editEmail = viewGroup.findViewById(R.id.edtContent);
        findBtn = viewGroup.findViewById(R.id.findBtn);
        recyclerView = viewGroup.findViewById(R.id.friendRecyclerView);
        toolbar=viewGroup.findViewById(R.id.toolbar);
        checkBox=viewGroup.findViewById(R.id.checkbox);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase =FirebaseDatabase.getInstance();
        //user - friend
        friendsDBRef = firebaseDatabase.getReference("users").child(firebaseUser.getUid()).child("friends");
        userDBRef = firebaseDatabase.getReference("users");

        friendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSearchBar();
            }

        });
        findBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                addFriend();
            }
        });
        // 데이터베이스에서 나의 친구목록을 리스너 통하여 데이터를 가져온다.
        addFriendListener();
        friendsListAdapter = new FriendsListAdapter();
        recyclerView.setAdapter(friendsListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getContext(),
                new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //유저의 정보를 가져와서 Snackbar를 통해 메시지 띄움
                        final User friend = friendsListAdapter.getItem(position);
                        Snackbar.make(view,friend.getName()+"님과 대화를 하시겠습니까?",
                                Snackbar.LENGTH_LONG).setAction("예",new View.OnClickListener(){

                            @Override
                            public void onClick(View v) {
                                Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
                                chatIntent.putExtra("uid",friend.getUid());
                                chatIntent.putExtra("uids", friendsListAdapter.getSelectedUids());
                                startActivityForResult(chatIntent, ChattingFragment.JOIN_ROOM_REQUEST_CODE);
                            }
                        }).show();
                    }
                }));

        return viewGroup;
    }


    public void toggleSearchBar(){
        searchArea.setVisibility( searchArea.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE );
    }
    /*public void toggleSelectionMode(){
        //현재 checkbox가 생성이 되어있는지 확인하는 코드
        friendsListAdapter.setSelectionMode(friendsListAdapter.getSelectionMode()
                ==FriendsListAdapter.SELECTION_MODE?FriendsListAdapter.UNSELECTION_MODE:
                FriendsListAdapter.SELECTION_MODE);
    }*/
    public void addFriend(){
        //입력된 이메일
    final String inputEmail = editEmail.getText().toString();
        if(inputEmail.isEmpty()){
            //비어있으면
            Snackbar.make(searchArea,"이메일을 입력해주세요", Snackbar.LENGTH_SHORT).show();
            return;
        }
        //입력 이메일이 사용자 이메일과 같을 때
        if (inputEmail.equals(firebaseUser.getEmail())) {
            Snackbar.make(searchArea,"자기 자신은 친구로 등록할 수 없습니다.",Snackbar.LENGTH_SHORT).show();
         return;
        }
        //이메일이 정상이라면 나의 정보를 조회 - 이미 등록된 친구인지를 판단
        friendsDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //해당 되는 Item 나옴
                Iterable<DataSnapshot> friendsIterable = dataSnapshot.getChildren();
                Iterator<DataSnapshot> friendsIterator = friendsIterable.iterator();
                while(friendsIterator.hasNext()) {//데이터가 있다면
                    User user = friendsIterator.next().getValue(User.class);
                    if (user.getEmail().equals(inputEmail)) {
                        Snackbar.make(searchArea, "이미 등록된 친구입니다.", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                }
                    //users db에 존재 하지 않는 이메일이라면, 가입하지 않는 친구라는 메세지
                    userDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> userIterator = dataSnapshot.getChildren().iterator();
                            int userCount = (int)dataSnapshot.getChildrenCount(); // getChildrenCount는 long형
                            int loopCount = 1;


                        while(userIterator.hasNext()){
                            //현재 가리키고 있는 user의 정보
                            final User currenUser = userIterator.next().getValue(User.class);
                            //유저에 실제 존재하는 사람인지 확인
                            if(inputEmail.equals(currenUser.getEmail())){
                                //친구 등록 로직
                                friendsDBRef.push().setValue(currenUser, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        //친구가 정상적으로 등록되었다면 상대방을 등록 + 나의 정보 등록
                                        userDBRef.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);
                                                userDBRef.child(currenUser.getUid()).child("friends").push().setValue(user);
                                                Snackbar.make(searchArea, "친구 등록이 완료되었습니다.", Snackbar.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }

                                });

                            }else{
                                if(loopCount++ >= userCount){
                                    Snackbar.make(searchArea,"가입을 하지 않은 친구입니다.",Snackbar.LENGTH_SHORT).show();
                                    return;
                            }

                            }
                            //총 사용자의 명수 == loopCount
                            //등록된 사용자가 없다는 메시지를 출력
                        }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void addFriendListener(){
        friendsDBRef.addChildEventListener(new ChildEventListener() { //친구가 추가/변경/이동/제거되었을 때
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //추가
                 User friend = dataSnapshot.getValue(User.class);
                 drawUI(friend);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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
        });
    }
    private void drawUI(User friend){
        friendsListAdapter.addItem(friend);

    }
}
