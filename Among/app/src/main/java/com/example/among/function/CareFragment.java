package com.example.among.function;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.among.R;
import com.example.among.function.detail.CareDetailFragment1;
import com.example.among.function.detail.CareDetailFragment2;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CareFragment extends Fragment {
    RecyclerView card_list;

    MyReplaceFragment myReplaceFragment;
    public CareFragment() {
        // Required empty public constructor
    }
    public CareFragment(MyReplaceFragment myReplaceFragment){
        this.myReplaceFragment = myReplaceFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewGroup = (View)LayoutInflater.from(getContext()).inflate(R.layout.fragment_care, container, false);
        card_list = (RecyclerView)viewGroup.findViewById(R.id.card_list);

        ArrayList<NurseViewItem> recycle_card_data = new ArrayList<NurseViewItem>();
        NurseViewItem[] item = new NurseViewItem[2];
        item[0] = new NurseViewItem("노인의료복지 시설");
        item[1] = new NurseViewItem("재가노인복지시설");

        for(int i=0;i<2;i++){
            recycle_card_data.add(item[i]);
        }

        CareViewAdapter adapter = new CareViewAdapter(this, R.layout.fragment_care,recycle_card_data);

        adapter.setOnItemClickListener(new CareViewAdapter.MyOnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Fragment fragment = null;
                switch (pos){
                    case 0:
                        fragment = new CareDetailFragment1();
                        break;
                    case 1:
                        fragment = new CareDetailFragment2();
                        break;
                }
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
