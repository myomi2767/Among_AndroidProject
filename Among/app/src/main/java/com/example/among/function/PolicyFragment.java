package com.example.among.function;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.among.R;

import java.util.ArrayList;


public class PolicyFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    RecyclerView card_list;
    private com.example.among.function.PolicyFragment PolicyFragment;

    public PolicyFragment(){

    }
    public static com.example.among.function.PolicyFragment newInstance(String param1, String param2) {
        com.example.among.function.PolicyFragment fragment = new PolicyFragment();
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
        item[0] = new PolicyViewItem(R.drawable.together,"정책 1","간단요약1");
        item[1] = new PolicyViewItem(R.drawable.together,"정책 2","간단요약2");
        item[2] = new PolicyViewItem(R.drawable.together,"정책 3","간단요약3");
        item[3] = new PolicyViewItem(R.drawable.together,"정책 4","간단요약4");
        item[4] = new PolicyViewItem(R.drawable.together,"정책 5","간단요약5");
        item[5] = new PolicyViewItem(R.drawable.together,"정책 6","간단요약6");

        for(int i=0;i<6;i++){
            recycle_card_data.add(item[i]);
        }

        PolicyViewAdapter adapter = new PolicyViewAdapter(PolicyFragment,
                R.layout.fragment_policy,recycle_card_data);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        card_list.setHasFixedSize(true);
        card_list.setLayoutManager(manager);
        card_list.setAdapter(adapter);


        return viewGroup;
    }


}
