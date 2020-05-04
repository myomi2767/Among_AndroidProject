package com.example.among.function;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.among.R;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class PolicyCareFragment extends Fragment implements MyReplaceFragment{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TabLayout tab;

    HealthFragment healthFragment;
    NurseFragment nurseFragment;
    CareFragment careFragment;
    PolicyFragment policyFragment;

    public PolicyCareFragment() {
        // Required empty public constructor
    }

    public static PolicyCareFragment newInstance(String param1, String param2) {
        PolicyCareFragment fragment = new PolicyCareFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onClick(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.content_container,fragment).commit();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewGroup = (View)LayoutInflater.from(getContext()).inflate(R.layout.fragment_policy_care, container, false);

        policyFragment = new PolicyFragment(this);
        healthFragment  = new HealthFragment(this);
        nurseFragment  = new NurseFragment(this);
        careFragment = new CareFragment(this);

        tab = viewGroup.findViewById(R.id.function_tab);

        getChildFragmentManager().beginTransaction()
                .replace(R.id.content_container,policyFragment).commit();

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment fragment = null;
                if(position==0){
                    //노인정책
                    fragment = policyFragment;
                }else if(position==1){
                    //노인복지
                    fragment = nurseFragment;
                }else if(position==2){
                    //요양보험제도
                    fragment = healthFragment;
                }else if(position==3){
                    //요양보험운영
                    fragment = careFragment;
                }
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.content_container,fragment).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        // Inflate the layout for this fragment
        return viewGroup;
    }


}
