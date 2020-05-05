
package com.example.among.children;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
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
import com.example.among.function.PolicyCareFragment;
import com.example.among.function.PolicyFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class childrenActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children);

        //앱바
        toolbar=findViewById(R.id.toolbar);
        getSupportActionBar();


        //바텀 네비
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigation.setSelectedItemId(R.id.navigation_home);

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null); //이게 쌓이는거
        transaction.commit();
    }

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

}




