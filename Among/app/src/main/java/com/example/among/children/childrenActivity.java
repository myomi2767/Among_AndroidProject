
package com.example.among.children;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.among.R;
import com.example.among.calendar.CalendarFragment;
import com.example.among.chatting.ChattingFragment;
import com.example.among.chatting.FriendFragment;
import com.example.among.children.fragment.chatFragment;
import com.example.among.children.fragment.communityFragment;
import com.example.among.children.fragment.homeFragment;
import com.example.among.children.fragment.scheduleFragment;
import com.example.among.children.map.familyChat.FamilychatMainActivity;
import com.example.among.function.PolicyCareFragment;
import com.example.among.function.PolicyFragment;
import com.example.among.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class childrenActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children);

        //앱바
        toolbar=findViewById(R.id.toolbarChild);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar();


        //바텀 네비
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigation.setSelectedItemId(R.id.navigation_home);

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        //transaction.addToBackStack(null); //이게 쌓이는거
        transaction.commit();
    }

    //바텀 네비
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home :
                            openFragment(FriendFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_wellfare:
                            openFragment(PolicyCareFragment.newInstance("",""));
                            return true;
                        case R.id.navigation_chat:
                            openFragment(ChattingFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_community:
                            openFragment(communityFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_question:
                            openFragment(CalendarFragment.newInstance("", ""));
                            return true;
                    }
                    return false;
                }
            };
    //FloatingActionButton을 눌렀을 때 홈화면이 보이도록 구현
    public void home_btn(View v){
        openFragment(FriendFragment.newInstance("", ""));
    }
    //툴바 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_child_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_sign_out:{
                signOut();
                return true;
            }
            case R.id.action_familyMaP:{
                //가족 지도
                popup();
                return true;
            }
            default:{
                return super.onOptionsItemSelected(item);
            }
        }

    }
    //앱바 로그아웃
    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    //앱바 지도보기
    public void popup(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(childrenActivity.this);
        //AlertDialog의 타이틀을 정의
        builder.setTitle("데이터입력");

        //AlertDialog에 보여질 화면을 inflate
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.input, null);//뷰를 따로 만들어 작업

        //AlertDialog에 추가할 버튼을 정의
        builder.setPositiveButton("확인", new DialogListener());
        builder.setNegativeButton("취소",new DialogListener());
        //AlertDialog에 화면 설정. 붙이기
        builder.setView(dialogView);
        builder.show();
    }
    //다이얼로그
    class DialogListener implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialog, int which) {

            AlertDialog inputAlert = (AlertDialog)dialog;
            EditText input = inputAlert.findViewById(R.id.input);
            String data = input.getText().toString();

            Intent intent = new Intent(childrenActivity.this, FamilychatMainActivity.class);

            //intent에 공유할 데이터 저장
            intent.putExtra("Chatrooms",
                    data);
            startActivity(intent);

        }
    }

}




