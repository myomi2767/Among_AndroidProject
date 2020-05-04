package com.example.among.function;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.among.R;
import com.example.among.function.detail.HealthDetailFragment1;
import com.example.among.function.detail.HealthDetailFragment2;
import com.example.among.function.detail.HealthDetailFragment3;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HealthFragment extends Fragment {
    RecyclerView card_list;
    MyReplaceFragment myReplaceFragment;
    public HealthFragment() {
        // Required empty public constructor
    }
    public HealthFragment(MyReplaceFragment myReplaceFragment){
        this.myReplaceFragment = myReplaceFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewGroup = (View)LayoutInflater.from(getContext()).inflate(R.layout.fragment_health, container, false);
        card_list = (RecyclerView)viewGroup.findViewById(R.id.card_list);

        ArrayList<NurseViewItem> recycle_card_data = new ArrayList<NurseViewItem>();
        NurseViewItem[] item = new NurseViewItem[3];
        item[0] = new NurseViewItem("정책의 이해");
        item[1] = new NurseViewItem("장기요양위원회");
        item[2] = new NurseViewItem("장기요양심판위원회");

        for(int i=0;i<3;i++){
            recycle_card_data.add(item[i]);
        }

        HealthViewAdapter adapter = new HealthViewAdapter(this, R.layout.fragment_health,recycle_card_data);

        adapter.setOnItemClickListener(new HealthViewAdapter.MyOnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Fragment fragment = null;
                switch (pos){
                    case 0:
                        fragment = new HealthDetailFragment1();
                        break;
                    case 1:
                        fragment = new HealthDetailFragment2();
                        break;
                    case 2:
                        fragment = new HealthDetailFragment3();
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
