package com.example.among.children.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.among.R;
import com.example.among.children.user.ProfileAdapter;
import com.example.among.children.user.ProfileItem;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public homeFragment() {

    }

    public static homeFragment newInstance(String param1, String param2) {
        homeFragment fragment = new homeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_child_first_page, container, false);
        ListView listView = view.findViewById(R.id.listContent);

       /* ImageView map = getView().findViewById(R.id.profile_map);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });*/

        //1. 리스트에 출력할 데이터
        //각각 하나의 아이템
        ProfileItem profileItem;
        //여러 아이템을 모을 리스트
        ArrayList<ProfileItem> profilelist = new ArrayList<ProfileItem>();

        //각각 하나의 아이템에 데이터 넣기
        profileItem= new ProfileItem(R.drawable.a8,"아버지","우리 3조", R.drawable.map);
        profilelist.add(profileItem);
        profileItem= new ProfileItem(R.drawable.a2,"어머니","화이팅", R.drawable.map);
        profilelist.add(profileItem);
        profileItem= new ProfileItem(R.drawable.a5,"짱아","짝짝짝!!!", R.drawable.map);
        profilelist.add(profileItem);
        profileItem= new ProfileItem(R.drawable.a9,"흰둥이","짝짝짝!!!", R.drawable.map);
        profilelist.add(profileItem);
        profileItem= new ProfileItem(R.drawable.a3,"유리","짝짝짝!!!", R.drawable.map);
        profilelist.add(profileItem);
        profileItem= new ProfileItem(R.drawable.a4,"훈이","짝짝짝!!!", R.drawable.map);
        profilelist.add(profileItem);
        profileItem= new ProfileItem(R.drawable.a6,"철수","짝짝짝!!!", R.drawable.map);
        profilelist.add(profileItem);
        profileItem= new ProfileItem(R.drawable.a7,"맹구","짝짝짝!!!", R.drawable.map);
        profilelist.add(profileItem);


        //2. 사용자정의 어댑터 객체생성
        ProfileAdapter adapter =
                new ProfileAdapter(getActivity(),
                        R.layout.activity_child_first_page_row,profilelist);
        //3. ListView에 어댑터 연결
        listView.setAdapter(adapter);
        return view;
    }
}