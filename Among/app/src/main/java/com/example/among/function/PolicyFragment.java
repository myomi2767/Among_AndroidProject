package com.example.among.function;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.among.R;
import com.example.among.function.detail.PolicyDetailFragment1;
import com.example.among.function.detail.PolicyDetailFragment2;
import com.example.among.function.detail.PolicyDetailFragment3;
import com.example.among.function.detail.PolicyDetailFragment4;
import com.example.among.function.detail.PolicyDetailFragment5;
import com.example.among.function.detail.PolicyDetailFragment6;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


public class PolicyFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    RecyclerView card_list;
    private PolicyFragment PolicyFragment;

    MyReplaceFragment myReplaceFragment;
    public PolicyFragment(){

    }
    public PolicyFragment(MyReplaceFragment myReplaceFragment){
        this.myReplaceFragment = myReplaceFragment;
    }
    public static PolicyFragment newInstance(String param1, String param2) {
        PolicyFragment fragment = new PolicyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewGroup = (View)LayoutInflater.from(getContext()).inflate(R.layout.fragment_policy, container, false);
        card_list = (RecyclerView)viewGroup.findViewById(R.id.card_list);

        ArrayList<PolicyViewItem> recycle_card_data = new ArrayList<PolicyViewItem>();
        PolicyViewItem[] item = new PolicyViewItem[6];
        item[0] = new PolicyViewItem("치매검진사업");
        item[1] = new PolicyViewItem("치매치료관리비지원사업");
        item[2] = new PolicyViewItem("노인실명예방관리사업");
        item[3] = new PolicyViewItem("노인맞춤돌봄서비스");
        item[4] = new PolicyViewItem("노인돌봄종합서비스사업");
        item[5] = new PolicyViewItem("노인주거복지시설");

        for(int i=0;i<6;i++){
            recycle_card_data.add(item[i]);
        }

        PolicyViewAdapter adapter = new PolicyViewAdapter(this,
                R.layout.fragment_policy,recycle_card_data);

        adapter.setOnItemClickListener(new PolicyViewAdapter.MyOnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Fragment fragment = null;
                switch (pos){
                    case 0:
                        fragment = new PolicyDetailFragment1();
                        break;
                    case 1:
                        fragment = new PolicyDetailFragment2();
                        break;
                    case 2:
                        fragment = new PolicyDetailFragment3();
                        break;
                    case 3:
                        fragment = new PolicyDetailFragment4();
                        break;
                    case 4:
                        fragment = new PolicyDetailFragment5();
                        break;
                    case 5:
                        fragment = new PolicyDetailFragment6();
                        break;
                }
               /* getChildFragmentManager().beginTransaction()
                        .replace(R.id.content_container,fragment).commit();*/
               myReplaceFragment.onClick(fragment);
            }
        });

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        /*GridLayoutManager manager = new GridLayoutManager(getContext(),2);*/

        card_list.setHasFixedSize(true);
        card_list.setLayoutManager(manager);
        card_list.setAdapter(adapter);

        return viewGroup;
    }


}
