package com.example.among.function;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.among.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;


public class FunctionActivity extends AppCompatActivity {


    BottomNavigationView bottomNavigationView;
    CommunityFragment communityFragment = new CommunityFragment();
    NurseFragment nurseFragment = new NurseFragment();
    HealthFragment healthFragment = new HealthFragment();
    PolicyFragment policyFragment = new PolicyFragment();
    TabLayout tabLayout;
    //RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.function_main);

        bottomNavigationView = findViewById(R.id.bottom_navi);
        tabLayout = findViewById(R.id.function_tab);
      //  recyclerView = findViewById(R.id.list);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.item1) {

                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.function_container, policyFragment).commitAllowingStateLoss();
                    return true;
                }else if(menuItem.getItemId()==R.id.item2){
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.function_container, nurseFragment).commitAllowingStateLoss();
                    return true;
                }else if(menuItem.getItemId()==R.id.item3){
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.function_container, communityFragment).commitAllowingStateLoss();
                    return true;
                }

                return false;
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment fragment;

                if(position==0){
                    fragment = policyFragment;
                }else if(position ==1){
                    fragment = healthFragment;
                }else {
                    fragment = nurseFragment;
                }
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.function_container,fragment).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    public void btn_register(View view){
        Intent intent = new Intent(FunctionActivity.this,boardWrite.class);
        startActivity(intent);
    }
}





